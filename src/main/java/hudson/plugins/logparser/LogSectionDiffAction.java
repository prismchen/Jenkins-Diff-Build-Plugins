package hudson.plugins.logparser;

import java.io.IOException;

import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;

/**
 * LogSectionDiffAction is the action to diff two console outputs
 */
public class LogSectionDiffAction implements Action {
    final private Run<?, ?> owner;
    public String html;
    private String fileName;

    /**
     * Construct a LogSectionDiffAction
     * 
     * @param job
     * @throws IOException
     */
    public LogSectionDiffAction(Job<?, ?> job) throws IOException {
        int build1 = Integer.parseInt(Stapler.getCurrentRequest().getParameter("Build1"));
        int build2 = Integer.parseInt(Stapler.getCurrentRequest().getParameter("Build2"));

        this.owner = job.getBuildByNumber(build1);

        Run<?, ?> b1 = owner;
        Run<?, ?> b2 = job.getBuildByNumber(build2);

        LogSectionDiffWorker lsdw = new LogSectionDiffWorker(b1, b2);
        this.html = lsdw.writeSectionDiffToHTMLs();
        this.html += "<br><br>";

        fileName = "build_" + build1 + "_" + build2 + "_console_section_diff.html";
    }

    /**
     * @return the owner build
     */
    public Run<?, ?> getOwner() {
        return this.owner;
    }

    /**
     * @return result html page
     */
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

    @Override
    public String getIconFileName() {
        return "";
    }

    @Override
    public String getDisplayName() {
        return "SectionDiff Page";
    }

    @Override
    public String getUrlName() {
        return "logSectionDiffAction";
    }
}
