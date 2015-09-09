/*
 * This is free and unencumbered software released into the public domain.
 */
package org.rolesample;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class AuthZErrorPage extends WicketSampleBasePage
{
    /**
     * Constructor
     */
    public AuthZErrorPage(PageParameters pageParameters)
    {
        String value = pageParameters.get( "errorValue" ).toString();
        add( new Label( "errorValue", value ) );
        final Link actionLink = new Link( "home.link" )
        {
            @Override
            public void onClick()
            {
                setResponsePage( HomePage.class );
            }
        };
        add( actionLink );

    }
}
