package me.cortex.voxy.commonImpl;

import me.cortex.voxy.common.world.WorldEngine;
import me.cortex.voxy.commonImpl.importers.IDataImporter;

import java.util.function.Supplier;

public class ImportManager {
    public boolean makeAndRunIfNone(WorldEngine engine, Supplier<IDataImporter> factory) {
       return false;
    }

    protected class ImportTask {

        protected ImportTask(IDataImporter importer) {
        }

        protected void onCompleted(int total) {
        }

        protected void shutdown() {
        }
    }

    protected synchronized ImportTask createImportTask(IDataImporter importer) {
        return new ImportTask(importer);
    }
}
