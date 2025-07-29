/*
 * © 2025 iamfortress.net
 */
/*
 * © 2025 iamfortress.net
 */
package org.rolesample;

import org.apache.directory.fortress.web.control.WicketSession;
import org.apache.wicket.Session;
import org.apache.wicket.csp.CSPDirective;
import org.apache.wicket.csp.CSPDirectiveSrcValue;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.settings.ExceptionSettings;
import org.apache.wicket.settings.RequestCycleSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * Role Engineering Sample WebApplication impl.
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class WicketApplication extends WebApplication
{

	// Load Fortress session into wicket session container:
	@Override
	public Session newSession(Request request, Response response)
	{
		return new WicketSession(request);
	}
	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();

		// Enable injection of fortress spring beans:
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		getMarkupSettings().setStripWicketTags(true);
		getRequestCycleSettings().setRenderStrategy( RequestCycleSettings.RenderStrategy.ONE_PASS_RENDER);
		// Route runtime exceptions to fortress error page:
		getApplicationSettings().setInternalErrorPage( ErrorPage.class );
		// show internal error page rather than default developer page
		getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);

		getMarkupSettings().setStripWicketTags(true);
		getCspSettings().blocking().clear()
				.add(CSPDirective.STYLE_SRC, CSPDirectiveSrcValue.SELF)
				.add(CSPDirective.STYLE_SRC, CSPDirectiveSrcValue.UNSAFE_INLINE)
				.add(CSPDirective.SCRIPT_SRC, CSPDirectiveSrcValue.SELF)
				.add(CSPDirective.SCRIPT_SRC, CSPDirectiveSrcValue.UNSAFE_EVAL)
				.add(CSPDirective.SCRIPT_SRC, CSPDirectiveSrcValue.UNSAFE_INLINE);
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}
}
