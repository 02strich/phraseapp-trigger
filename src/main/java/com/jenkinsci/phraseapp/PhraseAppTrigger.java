package com.jenkinsci.phraseapp;
import antlr.ANTLRException;

import com.jenkinsci.phraseapp.Utils.PhraseAppUpdater;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.Node;

import java.io.File;
import net.sf.json.JSONObject;

import org.jenkinsci.lib.xtrigger.AbstractTrigger;
import org.jenkinsci.lib.xtrigger.XTriggerDescriptor;
import org.jenkinsci.lib.xtrigger.XTriggerException;
import org.jenkinsci.lib.xtrigger.XTriggerLog;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
/**
 *
 * @author 02strich
 */
public class PhraseAppTrigger extends AbstractTrigger {

	private static final long serialVersionUID = 8643222464707891927L;

	@DataBoundConstructor
    public PhraseAppTrigger(String cronTabSpec) throws ANTLRException {
        super(cronTabSpec);
        
    }
    
    @Override
    protected File getLogFile() {
        return new File(job.getRootDir(), "phraseapp-polling.log");
    }

    @Override
    protected boolean requiresWorkspaceForPolling() {
        return true;
    }

    @Override
    protected String getName() {
        return "PhraseApp";
    }

    @Override
    protected Action[] getScheduledActions(Node node, XTriggerLog xtl) {
        return new Action[0];
    }

    @Override
    protected boolean checkIfModified(Node node, XTriggerLog xtl) throws XTriggerException {
        AbstractProject project = (AbstractProject) job;
        PhraseAppUpdater updater = new PhraseAppUpdater(project.getSomeWorkspace(), xtl);
        return updater.performUpdate();
    }

    @Override
    protected String getCause() {
        return "Translations updated.";
    }
    
    @Override
    public PhraseAppTriggerDescriptor getDescriptor() {
        return (PhraseAppTriggerDescriptor) Hudson.getInstance().getDescriptorOrDie(getClass());
    }

    @Override
    public Action getProjectAction() {
        return new PhraseAppTriggerAction((AbstractProject)job, getLogFile());
    }
    
    @Extension
    public static final class PhraseAppTriggerDescriptor extends XTriggerDescriptor {
        @Override
        public String getDisplayName() {
            return "Build on PhraseApp updates";
        }
    }
}
