/**
 * Copyright 2014 Antonio Robirosa

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.systemsetup;

import de.hybris.platform.constants.CoreConstants;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Contains auxiliary methods related with the Hybris extensions.
 * 
 * TODO Find a clear responsibility for this class.
 * 
 * @author arobirosa
 * 
 */
@Scope("tenant")
@Component
public class ExtensionHelper
{
	/**
	 * Returns a boolean indicating if the current extension is the first one which is run during an update running
	 * system.
	 * 
	 * @param context
	 *           Required.
	 * @return boolean True if it is the first one.
	 */

	public boolean isFirstExtension(final SystemSetupContext context)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("context", context);
		//There must be a better way to find out which one is the first extension
		return CoreConstants.EXTENSIONNAME.equalsIgnoreCase(context.getExtensionName());
	}
}