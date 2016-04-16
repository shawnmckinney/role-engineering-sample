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
        LOG.info( "Begin RoleSampleSeleniumITCase" );
        driver.get( baseUrl );

        // User ssmith, has access to Buyers page:
        login( GlobalIds.BUYER_USER, "password" );
        TUtils.sleep( 1 );
        //doNegativeButtonTests( GlobalIds.PAGE_1, GlobalIds.USER_123, GlobalIds.BTN_PAGE_1 );
        //doActivateTest( GlobalIds.USER_123, GlobalIds.PAGE_1, GlobalIds.BTN_PAGE_1, GlobalIds.ROLE_PAGE1_123, null, "123", "789", false );
        //TUtils.sleep( 1 );
        //doNegativeDataTest( GlobalIds.BTN_PAGE_1, GlobalIds.ROLE_PAGE1_123, "456");
        //TUtils.sleep( 1 );
        //doNegativeDataTest( GlobalIds.BTN_PAGE_1, GlobalIds.ROLE_PAGE1_123, "789");
        //driver.findElement( By.linkText( GlobalIds.PAGE_BUYERS_LINK ) ).click();
        //TUtils.sleep( 1 );
        doNegativeButtonTest( GlobalIds.BUYER_USER, GlobalIds.PAGE_BUYERS, GlobalIds.BTN_ITEM_SHIP );
        doNegativeButtonTest( GlobalIds.BUYER_USER, GlobalIds.PAGE_BUYERS, GlobalIds.BTN_AUCTION_CREATE );
        logout( GlobalIds.BUYER_USER );
    }

/*
    private void doNegativeSellerButtonTests( String linkName, String userId, String pageId )
    {
        info( "Negative Button test for user: " + userId + ", linkName: " + linkName );
        if(linkName != null)
            driver.findElement( By.linkText( linkName ) ).click();
        doNegativeButtonTest( userId, pageId, GlobalIds.BTN_ITEM_SHIP );
        doNegativeButtonTest( userId, pageId, GlobalIds.BTN_AUCTION_CREATE );
    }
*/

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
/*
    private void doPositiveDataTest(String buttonPage, String activateRole, String data)
    {
        info( "Postive Data test for role: " + activateRole + ", customer: " + data );
        driver.findElement( By.id( GlobalIds.CUSTOMER_EF_ID ) ).clear();
        driver.findElement( By.id( GlobalIds.CUSTOMER_EF_ID ) ).sendKeys( data );
        driver.findElement( By.name( buttonPage + "." + GlobalIds.SEARCH ) ).click();
    }

    private void doNegativeDataTest(String buttonPage, String activateRole, String data)
    {
        info( "Negative Data test for role: " + activateRole + ", customer: " + data );
        driver.findElement( By.id( GlobalIds.CUSTOMER_EF_ID ) ).clear();
        driver.findElement( By.id( GlobalIds.CUSTOMER_EF_ID ) ).sendKeys( data );
        driver.findElement( By.name( buttonPage + "." + GlobalIds.SEARCH ) ).click();
        if(!processPopup("Unauthorized"))
            fail("doActivateTest Unauthorized data Test Failed: " + buttonPage + "." + GlobalIds.SEARCH);
    }
*/

/*
    private void doPositiveButtonTests( String linkName, String pageId )
    {
        info( "Postive Button test for " + linkName );
        if(linkName != null)
            driver.findElement( By.linkText( linkName ) ).click();
        TUtils.sleep( 1 );
        // Click the buttons on the page
        doPositiveButtonTest(pageId, GlobalIds.ADD, pageId + "." + GlobalIds.ADD);
        doPositiveButtonTest(pageId, GlobalIds.UPDATE, pageId + "." + GlobalIds.UPDATE);
        doPositiveButtonTest(pageId, GlobalIds.DELETE, pageId + "." + GlobalIds.DELETE);
        doPositiveButtonTest(pageId, GlobalIds.SEARCH, pageId + "." + GlobalIds.SEARCH);
    }

*/

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

/*
    private void activateRole(String roleName)
    {
        info("Activate test for " + roleName);
        ( ( JavascriptExecutor ) driver ).executeScript( "$(document.getElementById('" + GlobalIds.INACTIVE_ROLES + "')).val('" + roleName + "');" );
        driver.findElement( By.name( GlobalIds.ROLES_ACTIVATE ) ).click();
    }
*/

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

/*
    private void doNegativeLinkTest( String linkName, String userId  )
    {
        info("Negative link:" + linkName + " test for " + userId);
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

        // Check that Spring security is enforcing page level security:
        String pageName = linkName;
        // convert from link name to page name for url:
        pageName = pageName.substring( 0, 1 ) + pageName.substring( 1 ).toLowerCase();
        String unauthorizedUrl = baseUrl + "/wicket/bookmarkable/com.mycompany." + pageName;
        driver.get( unauthorizedUrl );
        if(is403())
        {
            // pass
            TUtils.sleep( 1 );
            driver.navigate().back();
        }
        else
        {
            fail("Spring Security Test Failed URL: " + unauthorizedUrl + "." + GlobalIds.ADD);
        }
    }
*/

    public boolean is403()
    {
        try
        {
            driver.findElement(By.id("web_403"));
            return true;
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
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
        info("Logout " + userId);
        driver.findElement( By.linkText( "LOGOUT" ) ).click();
        LOG.info( "User: " + userId + " has logged OFF" );
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
