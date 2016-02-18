package hudson.plugins.logparser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
 * DependencyDiffAction is the action to diff dependencies in POM.xml between
 * two builds.
 */
public class DependencyDiffAction implements Action {
    private String html;
    private final Run<?, ?> owner;
    private String fileName;

    /**
     * Construct a dependency diff action
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
    public DependencyDiffAction(Job<?, ?> job, Launcher launcher, FilePath workspace)
            throws Exception {
        int build1 = Integer.parseInt(Stapler.getCurrentRequest().getParameter("Build1"));
        int build2 = Integer.parseInt(Stapler.getCurrentRequest().getParameter("Build2"));

        owner = job.getBuildByNumber(build1);

        Map<String, List<String>> pomcontent1 = SCMUtils.getFilesFromBuild("pom.xml",
                (AbstractProject<?, ?>) job, build1, launcher, workspace);
        Map<String, List<String>> pomcontent2 = SCMUtils.getFilesFromBuild("pom.xml",
                (AbstractProject<?, ?>) job, build2, launcher, workspace);

        String configPath = job.getConfigFile().getFile().getAbsolutePath();
        BufferedReader br = new BufferedReader(new FileReader(configPath));

        String line;
        String pomPath = "null";
        while ((line = br.readLine()) != null) {
            if (line.contains("pom.xml")) {
                pomPath = line.substring(line.indexOf("<rootPOM>") + 9, line.indexOf("</rootPOM>"))
                        .trim();
                break;
            }
        }
        List<String> contentlist1 = pomcontent1.get(pomPath);
        List<String> contentlist2 = pomcontent2.get(pomPath);

        InputStream in1 = getInStream(contentlist1);
        ArrayList<Dependency> deplist1 = DependencyDiffUtils.parsePom(in1);
        InputStream in2 = getInStream(contentlist2);
        ArrayList<Dependency> deplist2 = DependencyDiffUtils.parsePom(in2);
        br.close();

        this.html = DependencyDiffUtils.toHtml(deplist1, deplist2,
                DependencyDiffUtils.diff(deplist1, deplist2), build2, build1);
        this.fileName = "build_" + build1 + "_" + build2 + "_dependency_diff.html";

    }

    private InputStream getInStream(List<String> contentlist) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (String s : contentlist) {
            baos.write(s.getBytes());
        }
        byte[] bytes = baos.toByteArray();
        return new ByteArrayInputStream(bytes);
    }

    /**
     * @return the owner build of the action
     */
    public Run<?, ?> getOwner() {
        return this.owner;
    }

    /**
     * @return the diff result page
     */
    public String getHtml() {
        return this.html;
    }

    @JavaScriptMethod
    public String exportHtml() {
        return this.html;
    }

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
        return "DependencyDiff Page";
    }

    @Override
    public String getUrlName() {
        return "dependencyDiffAction";
    }
}
