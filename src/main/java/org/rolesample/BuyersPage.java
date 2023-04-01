/*
 * © 2023 iamfortress.net
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
    private String userId = getUserid();

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

            add( new Label( "label1", "Welcome Buyer : " + userId ) );

            add( new FtIndicatingAjaxButton( "item.bid" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target )
                {
                    logIt( target, "Item, Bid Pressed" );
                }
            } );

            add( new FtIndicatingAjaxButton( "item.buy" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target )
                {
                    logIt( target, "Item, Buy Pressed" );
                }
            } );

        }
    }
}
