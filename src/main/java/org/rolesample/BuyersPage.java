/*
 * This is free and unencumbered software released into the public domain.
 */
package org.rolesample;

import org.apache.directory.fortress.web.control.FtIndicatingAjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;

/**
 * Role Engineering Sample Buyer's Page
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class BuyersPage extends WicketSampleBasePage
{
    public BuyersPage()
    {
        add( new BuyersPageForm( "pageForm" ) );
    }

    /**
     * Page 1 Form
     */
    public class BuyersPageForm extends Form
    {
        BuyersPageForm( String id )
        {
            super( id );

            add( new Label( "label1", "This is the Buyer's Page" ) );

            add( new FtIndicatingAjaxButton( "item.bid" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    logIt( target, "Item, Bid Pressed" );
                }
            } );

            add( new FtIndicatingAjaxButton( "item.buy" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    logIt( target, "Item, Buy Pressed" );
                }
            } );

        }
    }
}
