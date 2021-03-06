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
package org.areco.ecommerce.deploymentscripts.impex.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.impex.ImpexImportException;
import org.areco.ecommerce.deploymentscripts.impex.ImpexImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

/**
 * This default implementation uses the impex importer. It allows the configuration of the locale.
 * 
 * @author arobirosa
 * 
 */
@Scope("tenant")
@Service
public class LocalizedImpexImportService implements ImpexImportService {

    public static final String IMPEX_LOCALE_CONF = "deploymentscripts.impex.locale";

    private static final Logger LOG = Logger.getLogger(LocalizedImpexImportService.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ImportService importService;

    /*
     * { @InheritDoc }
     */
    @Override
    public void importImpexFile(final File impexFile) throws ImpexImportException {
        ServicesUtil.validateParameterNotNullStandardMessage("impexFile", impexFile);
        try (InputStream inputStream = Files.newInputStream(Paths.get(impexFile.toURI()))) {
            importImpexFile(inputStream);
        } catch (final FileNotFoundException | NoSuchFileException e) {
            throw new ImpexImportException("Unable to find the file " + impexFile, e);
        } catch (final UnsupportedEncodingException e) {
            throw new ImpexImportException("The file " + impexFile + " must use the UTF-8 encoding.", e);
        } catch (final IOException e) {
            throw new ImpexImportException("There was an IO exception opening the file " + impexFile + ": " + e.getMessage(), e);
        }
    }

    private void importImpexFile(final InputStream inputStream) throws ImpexImportException {
        final ImportConfig importConfig = new ImportConfig();
        importConfig.setScript(new StreamBasedImpExResource(inputStream, DeploymentScript.DEFAULT_FILE_ENCODING));
        final String localeCode = configurationService.getConfiguration().getString(IMPEX_LOCALE_CONF);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Code of the impex locale: '" + localeCode + "'.");
        }
        if (localeCode != null && !localeCode.isEmpty()) {
            importConfig.setLocale(LocaleUtils.toLocale(localeCode));
        }
        importConfig.setEnableCodeExecution(true);

        final ImportResult importResult = importService.importData(importConfig);
        if (importResult.isSuccessful()) {
            return;
        } else {
            throw new ImpexImportException("The import of the impex file finished with errors. ");
        }
    }
}
