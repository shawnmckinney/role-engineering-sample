package org.rolesample;

import java.lang.String;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.*;
import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * This class uses apache selenium firefox driver to drive commander web ui
 */
public class RoleSampleSeleniumITCase
{
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();
    private static final Logger LOG = Logger.getLogger( RoleSampleSeleniumITCase.class.getName() );

    @Before
    public void setUp() throws Exception
    {
        FirefoxProfile ffProfile = new FirefoxProfile();
        ffProfile.setPreference( "browser.safebrowsing.malware.enabled", false );
        driver = new FirefoxDriver( ffProfile );
        driver.manage().window().maximize();

        // Use test local default:
        baseUrl = "http://localhost:8080";
       // baseUrl = "https://IL1SCOLSP102:8443";
                                    baseUrl += "/role-engineering-sample";
        driver.manage().timeouts().implicitlyWait( 2500, TimeUnit.MILLISECONDS );
    }

    private void info(String msg)
    {
        ( ( JavascriptExecutor ) driver ).executeScript( "$(document.getElementById('infoField')).val('" + msg + "');" );
    }

    @Test
    public void testCase1() throws Exception
    {
        LOG.info( "Begin RoleSampleSeleniumITCase Test Case #1" );
        driver.get( baseUrl );

        // User ssmith, has access to Buyers page:
        login( GlobalIds.BUYER_USER, "password" );
        TUtils.sleep( 1 );
        doNegativeLinkTest( GlobalIds.PAGE_SELLERS_LINK, GlobalIds.BUYER_USER );
        doNegativeButtonTest( GlobalIds.BUYER_USER, GlobalIds.PAGE_BUYERS, GlobalIds.BTN_ITEM_SHIP );
        doNegativeButtonTest( GlobalIds.BUYER_USER, GlobalIds.PAGE_BUYERS, GlobalIds.BTN_AUCTION_CREATE );
        doUserPositiveButtonTests( GlobalIds.PAGE_BUYERS );
        doBuyerPositiveButtonTests( GlobalIds.PAGE_BUYERS_LINK, GlobalIds.PAGE_BUYERS );
        logout( GlobalIds.BUYER_USER );

        // User rtaylor, has access to Sellers page:
        login( GlobalIds.SELLER_USER, "password" );
        TUtils.sleep( 1 );
        doNegativeLinkTest( GlobalIds.PAGE_BUYERS_LINK, GlobalIds.SELLER_USER );
        doNegativeButtonTest( GlobalIds.SELLER_USER, GlobalIds.PAGE_SELLERS, GlobalIds.BTN_ITEM_BID );
        doNegativeButtonTest( GlobalIds.SELLER_USER, GlobalIds.PAGE_BUYERS, GlobalIds.BTN_ITEM_BUY );
        doUserPositiveButtonTests( GlobalIds.PAGE_BUYERS );
        doSellerPositiveButtonTests( GlobalIds.PAGE_SELLERS_LINK, GlobalIds.PAGE_SELLERS );
        logout( GlobalIds.SELLER_USER );
    }

    @Test
    public void testCase2() throws Exception
    {
        LOG.info( "Begin RoleSampleSeleniumITCase Test Case #2" );
        driver.get( baseUrl );

        // User johndoe, has access to both Buyers and Sellers page:
        login( GlobalIds.BOTH_USER, "password" );
        TUtils.sleep( 1 );
        doNegativeLinkTest( GlobalIds.PAGE_SELLERS_LINK, GlobalIds.BOTH_USER );
        doNegativeButtonTest( GlobalIds.BOTH_USER, GlobalIds.PAGE_BUYERS, GlobalIds.BTN_ITEM_SHIP );
        doNegativeButtonTest( GlobalIds.BOTH_USER, GlobalIds.PAGE_BUYERS, GlobalIds.BTN_AUCTION_CREATE );
        doUserPositiveButtonTests( GlobalIds.PAGE_BUYERS );
        doBuyerPositiveButtonTests( GlobalIds.PAGE_BUYERS_LINK, GlobalIds.PAGE_BUYERS );

        // Now switch to sellers role:
        driver.findElement( By.name( GlobalIds.BTN_SWITCH_SELLER ) ).click();
        TUtils.sleep( 1 );
        doNegativeLinkTest( GlobalIds.PAGE_BUYERS_LINK, GlobalIds.BOTH_USER );
        doNegativeButtonTest( GlobalIds.SELLER_USER, GlobalIds.PAGE_SELLERS, GlobalIds.BTN_ITEM_BID );
        doNegativeButtonTest( GlobalIds.SELLER_USER, GlobalIds.PAGE_BUYERS, GlobalIds.BTN_ITEM_BUY );
        doUserPositiveButtonTests( GlobalIds.PAGE_BUYERS );
        doSellerPositiveButtonTests( GlobalIds.PAGE_SELLERS_LINK, GlobalIds.PAGE_SELLERS );
        logout( GlobalIds.BOTH_USER );
    }

    private void doNegativeButtonTest( String userId, String pageId, String buttonId )
    {
        info("Negative button test for " + buttonId + ", and " + userId);
        try
        {

            driver.findElement( By.linkText( GlobalIds.PAGE_BUYERS_LINK ) ).click();
            TUtils.sleep( 1 );
            driver.findElement( By.name( pageId + "." + buttonId ) ).click();
            fail("Negative Button Test Failed: " + pageId + "." + buttonId );
        }
        catch (org.openqa.selenium.NoSuchElementException e)
        {
            // pass
        }
    }

    private void doUserPositiveButtonTests( String pageId )
    {
        // Click the buttons on the page
        doPositiveButtonTest(pageId, GlobalIds.BTN_ACCOUNT_CREATE, pageId + "." + GlobalIds.BTN_ACCOUNT_CREATE);
        doPositiveButtonTest(pageId, GlobalIds.BTN_ITEM_SEARCH, pageId + "." + GlobalIds.BTN_ITEM_SEARCH);
    }

    private void doBuyerPositiveButtonTests( String linkName, String pageId )
    {
        if(linkName != null)
            driver.findElement( By.linkText( linkName ) ).click();
        TUtils.sleep( 1 );
        // Click the buttons on the page
        doPositiveButtonTest(pageId, GlobalIds.BTN_ITEM_BID, pageId + "." + GlobalIds.BTN_ITEM_BID);
        doPositiveButtonTest(pageId, GlobalIds.BTN_ITEM_BUY, pageId + "." + GlobalIds.BTN_ITEM_BUY);
    }

    private void doSellerPositiveButtonTests( String linkName, String pageId )
    {
        if(linkName != null)
            driver.findElement( By.linkText( linkName ) ).click();
        TUtils.sleep( 1 );
        // Click the buttons on the page
        doPositiveButtonTest(pageId, GlobalIds.BTN_AUCTION_CREATE, pageId + "." + GlobalIds.BTN_AUCTION_CREATE);
        doPositiveButtonTest(pageId, GlobalIds.BTN_ITEM_SHIP, pageId + "." + GlobalIds.BTN_ITEM_SHIP);
    }

    private boolean processPopup(String text)
    {
        boolean textFound = false;
        try
        {
            Alert alert = driver.switchTo ().alert ();
            //alert is present
            LOG.info( "Button Pressed:" + alert.getText() );
            if(alert.getText().contains( text ))
                textFound = true;

            alert.accept();
        }
        catch ( NoAlertPresentException n)
        {
            //Alert isn't present
        }
        return textFound;
    }

    private void doPositiveButtonTest(String pageId, String buttonId, String alertText)
    {
        info("Positive button test for " + pageId + ", " + buttonId);
        try
        {
            driver.findElement( By.name( pageId + "." + buttonId ) ).click();
        }
        catch(Exception e)
        {
            LOG.error( "activateRole Exception: " + e);
        }

        //TUtils.sleep( 1 );
        //if(!processPopup(alertText))
        //    fail("Button Test Failed: " + pageId + "." + buttonId);
    }

    private void login(String userId, String password)
    {
        driver.findElement( By.id( GlobalIds.USER_ID ) ).clear();
        driver.findElement( By.id( GlobalIds.USER_ID ) ).sendKeys( userId );
        driver.findElement( By.id( GlobalIds.PSWD_FIELD ) ).clear();
        driver.findElement( By.id( GlobalIds.PSWD_FIELD ) ).sendKeys( password );
        driver.findElement( By.name( GlobalIds.LOGIN ) ).click();
        LOG.info( "User: " + userId + " has logged ON" );
        info("Login User: " + userId);
    }

    private void logout(String userId)
    {
        info( "Logout " + userId );
        driver.findElement( By.linkText( "LOGOUT" ) ).click();
        LOG.info( "User: " + userId + " has logged OFF" );
    }

    private void doNegativeLinkTest( String linkName, String userId  )
    {
        //info( "Negative link test for userId: " + userId + ", linkName" + linkName );
        info( "Negative link test for " + userId + " on " + linkName);
        //info("Negative link test for " + linkName + ", and " + userId);
        //info("Negative button test for " + linkName);

        try
        {
            if(driver.findElement( By.linkText( linkName ) ).isEnabled())
            {
                fail("Negative Link Test Failed UserId: " + userId + " Link: " + linkName);
            }
            fail("Negative Button Test Failed UserId: " + userId + " Link: " + linkName);
        }
        catch (org.openqa.selenium.NoSuchElementException e)
        {
            // pass
        }
        try
        {
            if(driver.findElement( By.linkText( linkName ) ).isEnabled())
            {
                fail("Negative Link Test Failed UserId: " + userId + " Link: " + linkName);
            }
        }
        catch (org.openqa.selenium.NoSuchElementException e)
        {
            // pass
        }
    }

    private void nextPage(WebElement table, String szTableName)
    {
        table = driver.findElement(By.id( szTableName));
        List<WebElement> allRows = table.findElements(By.tagName("a"));
        for (WebElement row : allRows)
        {
            String szText = row.getText();
            if(szText.equals( "Go to the next page" ))
                row.click();
            LOG.debug( "row text=" + row.getText() );
        }
    }

    @After
    public void tearDown() throws Exception
    {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if ( !"".equals( verificationErrorString ) )
        {
            fail( verificationErrorString );
        }
    }

    private boolean isElementPresent( By by )
    {
        try
        {
            driver.findElement( by );
            return true;
        }
        catch ( NoSuchElementException e )
        {
            return false;
        }
    }

    private boolean isAlertPresent()
    {
        try
        {
            driver.switchTo().alert();
            return true;
        }
        catch ( NoAlertPresentException e )
        {
            return false;
        }
    }

    private String closeAlertAndGetItsText()
    {
        try
        {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if ( acceptNextAlert )
            {
                alert.accept();
            }
            else
            {
                alert.dismiss();
            }
            return alertText;
        }
        finally
        {
            acceptNextAlert = true;
        }
    }
}
