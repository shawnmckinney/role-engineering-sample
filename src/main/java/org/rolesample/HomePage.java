/*
 * © 2025 iamfortress.net
 */
package org.rolesample;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.markup.html.basic.Label;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * This Role Engineering Sample routes traffic here.  It displays list of page links at the top.
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class HomePage extends WicketSampleBasePage
{
    private static final Logger LOG = LoggerFactory.getLogger( HomePage.class.getName() );
    public HomePage()
    {
        HttpServletRequest servletReq = (HttpServletRequest)getRequest().getContainerRequest();
        Principal principal = servletReq.getUserPrincipal();
        // needed anytime container security checker allows requests in with old cookie (perhaps after server/app restart)::
        if(principal == null)
        {
            LOG.info( "user not logged in, route to login page instead" );
            // invalidate the session and force the user to log back on:
            servletReq.getSession().invalidate();
            getSession().invalidate();
            setResponsePage( LoginPage.class );
        }

        add(new Label("label1", "You have access to the link(s) above."));
    }
}
