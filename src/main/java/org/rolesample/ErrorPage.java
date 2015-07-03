/*
 * This is free and unencumbered software released into the public domain.
 */
package org.rolesample;


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * Errors routed here where it's job is to display out an error message.
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class ErrorPage extends WicketSampleBasePage
{
    public ErrorPage( Exception e )
    {
        add( new Label( "title", new Model<>( "Runtime Exception Occurred" ) ) );
        add( new Label( "message", new Model<>( e.getLocalizedMessage() ) ) );
    }
}
