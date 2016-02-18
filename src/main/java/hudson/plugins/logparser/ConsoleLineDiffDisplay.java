package hudson.plugins.logparser;

import java.io.IOException;

import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;

/**
 * This class gets user input, calls DiffToHtmlGenerator to generate the diff
 * result in html format, and then display the diff result on the build page
 */
public class ConsoleLineDiffDisplay implements Action {

    /**
     * the current build object
     */
    private final Run<?, ?> currentBuild;

    /**
     * html string that will be displayed on the page
     */
    private String html;

    private String fileName;

    /**
     * Create new ConsoleLineDiffDisplay object
     * 
     * @param job
     *            the Jenkins job
     */
    public ConsoleLineDiffDisplay(Job<?, ?> job) {
        int build1 = Integer.parseInt(Stapler.getCurrentRequest().getParameter("Build1"));
        int build2 = Integer.parseInt(Stapler.getCurrentRequest().getParameter("Build2"));

        this.currentBuild = job.getBuildByNumber(build1);

        Run<?, ?> b1 = currentBuild;
        Run<?, ?> b2 = job.getBuildByNumber(build2);

        String build1LogFileLocation = b1.getLogFile().getAbsolutePath();
        String build2FileLocation = b2.getLogFile().getAbsolutePath();

        DiffToHtmlGenerator d2h = null;
        try {
            d2h = new DiffToHtmlGenerator(build1LogFileLocation, build2FileLocation, build1,
                    build2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        html = d2h.generateHtmlString();

        fileName = "build_" + build1 + "_" + build2 + "_console_line_diff.html";
    }

    /**
     * returns the current build object
     * 
     * @return the current build object
     */
    public Run<?, ?> getOwner() {
        return this.currentBuild;
    }

    /**
     * returns html string
     * 
     * @return html string
     */
    public String getHtml() {
        return this.html;
    }

    /**
     * returns html content
     * 
     * @return html content
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

    /** * {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return "Console Line Diff Result Page";
    }

    /** * {@inheritDoc} */
    @Override
    public String getIconFileName() {
        return "";
    }

    /** * {@inheritDoc} */
    @Override
    public String getUrlName() {
        return "consoleLineDiffDisplay";
    }
}
