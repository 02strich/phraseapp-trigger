package com.jenkinsci.phraseapp.Utils;

import hudson.FilePath;

import java.io.IOException;

import org.jenkinsci.lib.xtrigger.XTriggerLog;

/**
 *
 * @author 02strich
 */
public class PhraseAppUpdater {
    private XTriggerLog log;
    private boolean updated;

    public PhraseAppUpdater(FilePath solutionDir, XTriggerLog log) {
        this.log = log;
        this.updated = false;
    }

    public boolean performUpdate() {
        try {
            checkVersions();
        } catch (Throwable ex) {
            log.error(ex.toString());
            return false;
        }
        return updated;
    }

    private void checkVersions() throws InterruptedException, IOException {
        updated = false;
    }
}