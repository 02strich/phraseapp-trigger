package com.jenkinsci.phraseapp;

import hudson.Util;
import hudson.console.AnnotatedLargeText;
import hudson.model.AbstractProject;
import hudson.model.Action;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.jelly.XMLOutput;

/**
 *
 * @author 02strich
 */
public class PhraseAppTriggerAction implements Action {
    private transient AbstractProject<?, ?> job;
    private transient File logFile;
    
    public PhraseAppTriggerAction(AbstractProject<?, ?> job, File logFile) {
        this.job = job;
        this.logFile = logFile;
    }
    
    public AbstractProject<?, ?> getOwner() {
        return job;
    }
    
    public String getIconFileName() {
        return "clipboard.gif";
    }

    public String getDisplayName() {
        return "PhraseApp Trigger Log";
    }

    public String getUrlName() {
        return "phraseapptriggerPollLog";
    }
    
    public String getLog() throws IOException {
        return Util.loadFile(getLogFile());
    }

    public File getLogFile() {
        return logFile;
    }

    public void writeLogTo(XMLOutput out) throws IOException {
        new AnnotatedLargeText<PhraseAppTriggerAction>(getLogFile(), Charset.defaultCharset(), true, this).writeHtmlTo(0, out.asWriter());
    }
}
