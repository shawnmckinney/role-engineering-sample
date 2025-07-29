/*
 * © 2025 iamfortress.net
 */
package org.rolesample;

import org.apache.directory.fortress.web.control.FtIndicatingAjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;

/**
 * Role Engineering Sample Seller's Page
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class SellersPage extends WicketSampleBasePage
{
    private String userId = getUserid();

    public SellersPage()
    {
        add( new SellersPageForm( "pageForm" ) );
    }

    /**
     * Page 1 Form
     */
    public class SellersPageForm extends Form
    {
        SellersPageForm( String id )
        {
            super( id );

            add( new Label( "label1", "Welcome Seller : " + userId ) );

            add( new FtIndicatingAjaxButton( "item.ship" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target )
                {
                    logIt( target, "Item, Ship Pressed" );
                }
            } );

            add( new FtIndicatingAjaxButton( "auction.create" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target )
                {
                    logIt( target, "Auction, Create Pressed" );
                }
            } );

        }
    }
}
