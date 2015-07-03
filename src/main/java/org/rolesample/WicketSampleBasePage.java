/*
 * This is free and unencumbered software released into the public domain.
 */
package org.rolesample;

import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.realm.J2eePolicyMgr;
import org.apache.directory.fortress.web.control.FtIndicatingAjaxButton;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpServletRequest;

/**
 * Base class for rolesample project.
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public abstract class WicketSampleBasePage extends WebPage
{
    // TODO STEP 8a: enable spring injection of fortress bean here:
    @SpringBean
    private AccessMgr accessMgr;
    @SpringBean
    private J2eePolicyMgr j2eePolicyMgr;

    public WicketSampleBasePage()
    {
        // TODO STEP 8b: uncomment call to enableFortress:
        try
        {
            SecUtils.enableFortress( this, ( HttpServletRequest ) getRequest().getContainerRequest(), j2eePolicyMgr,
                accessMgr );
        }
        catch (org.apache.directory.fortress.core.SecurityException se)
        {
            String error = "WicketSampleBasePage caught security exception : " + se;
            LOG.warn( error );
        }
        // TODO STEP 8c: change to FtBookmarkablePageLink:
        add( new BookmarkablePageLink( "sellerspage.link", SellersPage.class ) );
        add( new BookmarkablePageLink( "buyerspage.link", BuyersPage.class ) );
        final Link actionLink = new Link( "logout.link" )
        {
            @Override
            public void onClick()
            {
                setResponsePage(LogoutPage.class);
            }
        };
        add( actionLink );
        add( new UsersForm( "usersForm" ) );
        add( new Label( "footer", "This is free and unencumbered software released into the public domain." ) );
    }

    /**
     * Page 1 Form
     */
    public class UsersForm extends Form
    {
        public UsersForm( String id )
        {
            super( id );

            add( new FtIndicatingAjaxButton( "item.search" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    logIt( target, "Item, Search Pressed" );
                }
            } );

            add( new FtIndicatingAjaxButton( "account.create" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    logIt( target, "Account, Create Pressed" );
                }
            } );
        }
    }
    /**
     * Used by the child pages.
     *
     * @param target for modal panel
     * @param msg to log and display user info
     */
    protected void logIt(AjaxRequestTarget target, String msg)
    {
        info( msg );
        LOG.info( msg );
        target.appendJavaScript(";alert('" + msg + "');");
    }

    protected static final Logger LOG = Logger.getLogger( WicketSampleBasePage.class.getName() );
}