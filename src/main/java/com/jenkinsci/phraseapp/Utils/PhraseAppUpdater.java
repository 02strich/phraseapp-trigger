package com.jenkinsci.phraseapp.Utils;

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.remoting.VirtualChannel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jenkinsci.lib.xtrigger.XTriggerLog;

import com.google.common.base.Joiner;

/**
 *
 * @author 02strich
 */
public class PhraseAppUpdater {
    private XTriggerLog log;
    private boolean updated;
	private FilePath solutionDir;

	@SuppressWarnings("rawtypes")
	private Run lastBuild;

    public PhraseAppUpdater(@SuppressWarnings("rawtypes") AbstractProject project, XTriggerLog log) {
    	this.lastBuild = project.getLastBuild();
    	this.solutionDir = project.getSomeWorkspace();
        this.log = log;
        this.updated = false;
    }

    public boolean performUpdate() {
        try {
            checkForUpdate();
        } catch (Throwable ex) {
            log.error(ex.toString());
            return false;
        }
        return updated;
    }

    private void checkForUpdate() throws InterruptedException, IOException {
        updated = false;
        
        // read configuration from .phrase file
        String phraseConfigContent = solutionDir.act(new FileCallable<String>() {
			private static final long serialVersionUID = 1L;

			public String invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
				return Joiner.on("").join(Files.readAllLines(Paths.get(new File(f, ".phrase").getAbsolutePath()), StandardCharsets.UTF_8));
			};
        });
        
        // parse out secret
        JSONObject phraseConfig = JSONObject.fromObject(phraseConfigContent);
        String phraseSecret = phraseConfig.getString("secret");
        
        log.info(new StringBuilder().append("The last build was at:").append(lastBuild.getTimestampString2()).toString());
        
        // query PhraseApp API
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        URL url = new URL("https://phraseapp.com/api/v1/translations?auth_token=" + phraseSecret + "&updated_since=" + dateFormat.format(lastBuild.getTime()));
        URLConnection connection = url.openConnection();
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while((line = reader.readLine()) != null) {
        	builder.append(line);
        }

        // parse response
        JSONObject json = JSONObject.fromObject(builder.toString());
        for(Object key : json.keySet()) {
        	JSONArray translations = json.getJSONArray(key.toString());
        	if (translations.size() > 0) {
        		log.info(new StringBuilder().append("Found new/updated translations in locale ").append(key.toString()).toString());
        		updated = true;
        	}
        }
    }
}