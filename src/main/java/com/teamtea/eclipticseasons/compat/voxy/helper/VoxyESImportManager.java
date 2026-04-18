package com.teamtea.eclipticseasons.compat.voxy.helper;

import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import me.cortex.voxy.commonImpl.ImportManager;
import me.cortex.voxy.commonImpl.importers.IDataImporter;

public class VoxyESImportManager extends ImportManager {
    protected synchronized ImportTask createImportTask(IDataImporter importer) {
        return new ESImportTask(importer);
    }

    protected class ESImportTask extends ImportTask {
        protected ESImportTask(IDataImporter importer) {
            super(importer);
        }

        @Override
        protected void onCompleted(int total) {
            VoxyTool.releaseImporter();
            super.onCompleted(total);
        }

        @Override
        protected void shutdown() {
            VoxyTool.releaseImporter();
            super.shutdown();
        }
    }
}
