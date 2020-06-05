/*
 * This is free and unencumbered software released into the public domain.
 */
package org.rolesample;

import org.apache.commons.collections.CollectionUtils;
import org.apache.directory.fortress.core.*;
import org.apache.directory.fortress.realm.*;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;

import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.UserRole;
import org.apache.directory.fortress.web.control.FtBookmarkablePageLink;
import org.apache.directory.fortress.web.control.FtIndicatingAjaxButton;
import org.apache.directory.fortress.web.control.WicketSession;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.settings.ExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

/**
 * Base class for rolesample project.
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public abstract class WicketSampleBasePage extends WebPage
{
    // Fortress spring beans injected here:
    @SpringBean
    private AccessMgr accessMgr;
    @SpringBean
    private J2eePolicyMgr j2eePolicyMgr;
    @SpringBean
    private ReviewMgr reviewMgr;

    private final String BUYER = "Role_Buyers";
    private final String SELLER = "Role_Sellers";
    final String HOME_PAGE_OBJ = "org.rolesample.HomePage";
    final String SWITCH_SELLER_OP = "switchToSeller";
    final String SWITCH_BUYER_OP = "switchToBuyer";
    final String SWITCH_ROLES_OP = "switchRoles";

    public WicketSampleBasePage()
    {
        final Link actionLink = new Link( "logout.link" )
        {
            @Override
            public void onClick()
            {
                HttpServletRequest servletReq = ( HttpServletRequest ) getRequest().getContainerRequest();
                servletReq.getSession().invalidate();
                getSession().invalidate();
                setResponsePage( LoginPage.class );
            }
        };
        add( actionLink );
        HttpServletRequest servletReq = ( HttpServletRequest ) getRequest().getContainerRequest();
        // RBAC Security Processing:
        Principal principal = servletReq.getUserPrincipal();

        // Is this a Java EE secured page && has the User successfully authenticated already?
        boolean isSecured = principal != null;
        if ( isSecured )
        {
            if ( !SecUtils.isLoggedIn( this ) )
            {
                try
                {
                    String szPrincipal = principal.toString();
                    // Pull the RBAC session from the realm and assert intno the Web app's session along with user's
                    // perms:
                    SecUtils.initializeSession( this, j2eePolicyMgr, accessMgr, szPrincipal );
                }
                catch ( org.apache.directory.fortress.core.SecurityException se )
                {
                    throw new RuntimeException( se );
                }
            }
        }
        // Add FtBookmarkablePageLink will show link to user if they have the permission:
        add( new FtBookmarkablePageLink( "sellerspage.link", SellersPage.class ) );
        add( new FtBookmarkablePageLink( "buyerspage.link", BuyersPage.class ) );
        add( new UsersForm( "usersForm" ) );
        add( new Label( "footer", "This is free and unencumbered software released into the public domain." ) );
        add( new Label( GlobalIds.INFO_FIELD ));
    }

    /**
     * Page 1 Form
     */
    public class UsersForm extends Form
    {
        private List<UserRole> inactiveRoles;
        private List<UserRole> activeRoles;

        public UsersForm(String id)
        {
            super( id );

            add( new FtIndicatingAjaxButton( "item.search" )
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target)
                {
                    logIt( target, "Item, Search Pressed" );
                }
            } );

            add( new FtIndicatingAjaxButton( "account.create" )
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target)
                {
                    logIt( target, "Account, Create Pressed" );
                }
            } );

            loadActivatedRoleSets();
            addRoleActivationComboBoxesAndButtons();
        }

        /**
         * This loads the set of user's activated roles into a local page variable.  It is used for deactivate combo
         * box.
         */
        private void loadActivatedRoleSets()
        {
            Session session = SecUtils.getSession( this );
            if ( session != null )
            {
                LOG.info( "get assigned roles for user: " + session.getUserId() );
                try
                {
                    inactiveRoles = reviewMgr.assignedRoles( session.getUser() );
                    // remove inactiveRoles already activated:
                    for ( UserRole activatedRole : session.getRoles() )
                    {
                        inactiveRoles.remove( activatedRole );
                    }
                    LOG.info( "user: " + session.getUserId() + " inactiveRoles for activate list: " + inactiveRoles );
                    activeRoles = session.getRoles();
                }
                catch ( org.apache.directory.fortress.core.SecurityException se )
                {
                    String error = "SecurityException getting assigned inactiveRoles for user: " + session.getUserId();
                    LOG.error( error );
                }
            }
        }

        private void addRoleActivationComboBoxesAndButtons()
        {
            add( new SecureIndicatingAjaxButton( this, GlobalIds.BTN_SWITCH_BUYER, HOME_PAGE_OBJ, SWITCH_BUYER_OP )
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(AjaxRequestTarget target )
                {
                    getApplication().getExceptionSettings().setAjaxErrorHandlingStrategy( ExceptionSettings
                        .AjaxErrorStrategy.REDIRECT_TO_ERROR_PAGE );
                    if ( checkAccess( HOME_PAGE_OBJ, SWITCH_ROLES_OP ) )
                    {
                        switchToBuyer();
                        logIt( target, "Switch To Buyer Successful" );
                        setResponsePage( HomePage.class );
                    }
                    else
                    {
                        String msg = "You not authorized switch to Buyer";
                        PageParameters parameters = new PageParameters();
                        parameters.add( "errorValue", msg );
                        setResponsePage( AuthZErrorPage.class, parameters );
                    }
                }

                @Override
                protected void updateAjaxAttributes(AjaxRequestAttributes attributes)
                {
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        @Override
                        public CharSequence getFailureHandler(Component component)
                        {
                            String szRelocation = getLocationReplacement( ( HttpServletRequest ) getRequest()
                                .getContainerRequest() );
                            LOG.info( "HomePage.switchToBuyer Failure Handler, relocation string = " + szRelocation );
                            return szRelocation;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            } );

            add( new SecureIndicatingAjaxButton( this, GlobalIds.BTN_SWITCH_SELLER, HOME_PAGE_OBJ, SWITCH_SELLER_OP )
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(AjaxRequestTarget target )
                {
                    getApplication().getExceptionSettings().setAjaxErrorHandlingStrategy( ExceptionSettings
                        .AjaxErrorStrategy.REDIRECT_TO_ERROR_PAGE );
                    if ( checkAccess( HOME_PAGE_OBJ, SWITCH_ROLES_OP ) )
                    {
                        switchToSeller();
                        logIt( target, "Switch To Seller Successful" );
                        setResponsePage( HomePage.class );
                    }
                    else
                    {
                        String msg = "You are not authorized switch to Seller";
                        PageParameters parameters = new PageParameters();
                        parameters.add( "errorValue", msg );
                        setResponsePage( AuthZErrorPage.class, parameters );
                    }
                }

                @Override
                protected void updateAjaxAttributes(AjaxRequestAttributes attributes)
                {
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        @Override
                        public CharSequence getFailureHandler(Component component)
                        {
                            String szRelocation = getLocationReplacement( ( HttpServletRequest ) getRequest()
                                .getContainerRequest() );
                            LOG.info( "HomePage.switchToSeller Failure Handler, relocation string = " + szRelocation );
                            return szRelocation;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            } );

            Label inactivatedRoleString = new Label( "inactivatedRoleString", new PropertyModel<String>( this,
                "inactivatedRoleString" ) );
            add( inactivatedRoleString );
        }

        private void switchToSeller()
        {
            try
            {
                WicketSession session = ( WicketSession ) this.getSession();
                accessMgr.dropActiveRole( session.getSession(), new UserRole( session.getSession().getUserId(), BUYER
                ) );
                accessMgr.addActiveRole( session.getSession(), new UserRole( session.getSession().getUserId(), SELLER
                ) );
                SecUtils.getPermissions( this, accessMgr );
            }
            catch ( org.apache.directory.fortress.core.SecurityException se )
            {
                throw new RuntimeException( se );
            }
        }

        private void switchToBuyer()
        {
            try
            {
                WicketSession session = ( WicketSession ) this.getSession();
                accessMgr.dropActiveRole( session.getSession(), new UserRole( session.getSession().getUserId(),
                    SELLER ) );
                accessMgr.addActiveRole( session.getSession(), new UserRole( session.getSession().getUserId(), BUYER
                ) );
                SecUtils.getPermissions( this, accessMgr );
            }
            catch ( org.apache.directory.fortress.core.SecurityException se )
            {
                throw new RuntimeException( se );
            }
        }

        /**
         * Build a comma delimited String containing inactivated roles to be displayed in page label.
         *
         * @return String containing comma delimited inactivated roles
         */
        public String getInactivatedRoleString()
        {
            String szRoleStr = "";
            if ( CollectionUtils.isNotEmpty( inactiveRoles ) )
            {
                int ctr = 0;
                for ( UserRole role : inactiveRoles )
                {
                    if ( ctr++ > 0 )
                    {
                        szRoleStr += ", ";
                    }
                    szRoleStr += role.getName();
                }
            }
            return szRoleStr;
        }

        private String getLocationReplacement(HttpServletRequest servletRequest)
        {
            return "window.location.replace(\"" + servletRequest.getContextPath() + "\");";
        }
    }

    protected String getUserid()
    {
        String userid;
        WicketSession session = ( WicketSession ) this.getSession();
        Session ftSess = session.getSession();
        userid = ftSess.getUserId();
        return userid;
    }

    /**
     * Used by the child pages.
     *
     * @param target for modal panel
     * @param msg    to log and display user info
     */
    protected void logIt(AjaxRequestTarget target, String msg)
    {
        info( msg );
        LOG.info( msg );
        target.appendJavaScript( ";alert('" + msg + "');" );
    }

    protected static final Logger LOG = Logger.getLogger( WicketSampleBasePage.class.getName() );
}