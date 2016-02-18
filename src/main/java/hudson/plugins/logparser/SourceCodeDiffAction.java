package hudson.plugins.logparser;

import java.util.List;
import java.util.Map;

import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;

/**
 * SourceCodeDiffAction is the entry point for the functionality to diff source
 * codes between two builds.
 */
public class SourceCodeDiffAction implements Action {
    public final String html;
    private final Run<?, ?> owner;
    private final String fileName;

    /**
     * Construct a source code diff action
     *
     * @param job
     *            the project
     * @param launcher
     *            launcher
     * @param workspace
     *            path to workspace
     * @throws Exception
     *             if SCM fails to checkout
     */
    public SourceCodeDiffAction(Job<?, ?> job, Launcher launcher, FilePath workspace)
            throws Exception {

        int build1 = Integer.parseInt(Stapler.getCurrentRequest().getParameter("Build1"));
        int build2 = Integer.parseInt(Stapler.getCurrentRequest().getParameter("Build2"));

        this.owner = job.getBuildByNumber(build1);

        Map<String, List<String>> content1 = SCMUtils.getFilesFromBuild("*.java",
                (AbstractProject<?, ?>) job, build1, launcher, workspace);
        Map<String, List<String>> content2 = SCMUtils.getFilesFromBuild("*.java",
                (AbstractProject<?, ?>) job, build2, launcher, workspace);

        this.html = DiffToHtmlUtils.generateDiffHTML(build1, build2, "Source Code", content1,
                content2, null);
        fileName = "build_" + build1 + "_" + build2 + "_source_code_diff.html";
    }

    /**
     * @return the owner build
     */
    public Run<?, ?> getOwner() {
        return this.owner;
    }

    @Override
    public String getIconFileName() {
        return "";
    }

    @Override
    public String getDisplayName() {
        return "Source Code Diff Page";
    }

    @Override
    public String getUrlName() {
        return "sourceCodeDiffAction";
    }

    @JavaScriptMethod
    public String exportHtml() {
        return this.html;
    }

    /**
     * returns download file name
     *
     * @return download file name
     */

    @JavaScriptMethod
    public String exportFileName() {
        return this.fileName;
    }
}
