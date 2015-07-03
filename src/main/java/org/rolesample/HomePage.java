/*
 * This is free and unencumbered software released into the public domain.
 */
package org.rolesample;


import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;

/**
 * This Role Engineering Sample routes traffic here.  It displays list of page links at the top.
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class HomePage extends WicketSampleBasePage
{
    private static final Logger LOG = Logger.getLogger( HomePage.class.getName() );
    public HomePage()
    {
        add(new Label("label1", "You have access to the link(s) above."));
    }
}
