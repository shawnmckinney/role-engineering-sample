/*
 * © 2023 iamfortress.net
 */
package org.rolesample;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.markup.html.basic.Label;

import javax.servlet.http.HttpServletRequest;

/**
 * The Role Engineering Sample web app will route user's to this page for logout.
 * Although this page is important for page flow with regard to security, isn't itself secured by fortress or javaEE.
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class LogoutPage extends WicketSampleBasePage
{
    private static final Logger LOG = LoggerFactory.getLogger( LogoutPage.class.getName() );
    public LogoutPage()
    {
        HttpServletRequest servletReq = (HttpServletRequest)getRequest().getContainerRequest();
        LOG.info( "logout user, route to login page" );
        // invalidate the session and force the user to log back on:
        servletReq.getSession().invalidate();
        getSession().invalidate();
        setResponsePage( LoginPage.class );
        add(new Label("label1", "Select logout"));
    }
}
