package hudson.plugins.logparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;

/**
 * @since FIXME DiffBuildAction is the entry point of the functionality of diff
 *        build.
 * @author chanon
 *
 */
public class DiffBuildAction implements Action, Describable<DiffBuildAction> {

    private final Run<?, ?> build;
    private static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    private static List<Integer> allBuildNum;
    private static List<String> timeList;

    /**
     * The constructor for initializing fields and also get all build numbers in
     * the current job
     * 
     * @param build
     *            The current build
     * @param launcher
     *            This parameter is used for getting the source code via SVN API
     * @param workspace
     *            This parameter is used for getting the source code via SVN API
     */
    public DiffBuildAction(final Run<?, ?> build, final Launcher launcher,
            final FilePath workspace) {
        this.build = build;
        DescriptorImpl.launcher = launcher;
        DescriptorImpl.workspace = workspace;

        allBuildNum = new ArrayList<>();
        timeList = new ArrayList<>();
        int lastBuildNum = build.getParent().getLastBuild().number;
        int firstBuildNum = build.getParent().getFirstBuild().number;
        for (int i = lastBuildNum; i >= firstBuildNum; i--) {
            allBuildNum.add(i);
            timeList.add(build.getParent().getBuildByNumber(i).getTimestamp().getTime().toString());
        }
    }

    /**
     * Get current build
     * 
     * @return the current build object
     */
    public Run<?, ?> getOwner() {
        return this.build;
    }

    /**
     * To invoke Console Line Diff output page
     * 
     * @return the ConsoleLineDiffDisplay object
     */
    public ConsoleLineDiffDisplay getConsoleLineDiffDisplay() {
        Job<?, ?> job = build.getParent();
        return new ConsoleLineDiffDisplay(job);
    }

    /**
     * To invoke Log Section Diff output page
     * 
     * @return the LogSectionDiffAction object
     * @throws IOException
     */
    public LogSectionDiffAction getLogSectionDiffAction() throws IOException {
        Job<?, ?> job = build.getParent();
        return new LogSectionDiffAction(job);
    }

    /**
     * To invoke Dependency Diff output page
     * 
     * @return the DependencyDiffAction object
     * @throws Exception
     */
    public DependencyDiffAction getDependencyDiffAction() throws Exception {
        Job<?, ?> job = build.getParent();
        return new DependencyDiffAction(job, DescriptorImpl.launcher, DescriptorImpl.workspace);
    }

    /**
     * To invoke maven phase diff output page
     * 
     * @return the MavenPhaseDiffAction object
     * @throws IOException
     *             if any error occurs
     */
    public MavenPhaseDiffAction getMavenPhaseDiffAction() throws IOException {
        Job<?, ?> job = build.getParent();
        return new MavenPhaseDiffAction(job);
    }

    /**
     * To invoke Source Code Diff output page
     * 
     * @return the SourceCodeDiffAction object
     * @throws Exception
     */
    public SourceCodeDiffAction getSourceCodeDiffAction() throws Exception {
        Job<?, ?> job = build.getParent();
        return new SourceCodeDiffAction(job, DescriptorImpl.launcher, DescriptorImpl.workspace);
    }

    /** * {@inheritDoc} */
    @Override
    public String getIconFileName() {
        return "document.gif";
    }

    /** * {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return "Diff Against Other Build";
    }

    /** * {@inheritDoc} */
    @Override
    public String getUrlName() {
        return "diffbuild";
    }

    /** * {@inheritDoc} */
    @Override
    public Descriptor<DiffBuildAction> getDescriptor() {
        return DESCRIPTOR;
    }

    /**
     * 
     * The descriptor class for DiffBuildAction class
     * 
     * @author chanon
     *
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<DiffBuildAction> {

        private static Launcher launcher;
        private static FilePath workspace;

        /**
         * Display name for Choose Type of Diff dropdown
         */
        private final String TYPE_DIFF_DISPLAY[] = { "Console Output (By Line)",
                "Console Output (By Section)", "Source Code", "Maven Phase", "POM Dependency" };

        /**
         * Value for Choose Type of Diff dropdown, and these values have to
         * match with the url or each output page
         */
        private final String TYPE_DIFF_VALUE[] = { "consoleLineDiffDisplay", "logSectionDiffAction",
                "sourceCodeDiffAction", "mavenPhaseDiffAction", "dependencyDiffAction" };

        /**
         * Fill data in Choose A Build dropdown
         * 
         * @return the ListBoxModel object for Choose A Build dropdown
         */
        public ListBoxModel doFillBuild1Items() {
            ListBoxModel items = new ListBoxModel();
            for (int i = 0; i < allBuildNum.size(); i++) {
                if (i == 0) {
                    items.add(new Option(
                            String.format("build %s \t %s", allBuildNum.get(i), timeList.get(i)),
                            allBuildNum.get(i) + "", true));
                } else {
                    items.add(new Option(
                            String.format("build %s \t %s", allBuildNum.get(i), timeList.get(i)),
                            allBuildNum.get(i) + "", false));
                }
            }
            return items;
        }

        /**
         * Fill data in Choose Another Build dropdown
         * 
         * @return the ListBoxModel object for Choose Another Build dropdown
         */
        public ListBoxModel doFillBuild2Items() {
            ListBoxModel items = new ListBoxModel();
            for (int i = 0; i < allBuildNum.size(); i++) {
                if (i == 0) {
                    items.add(new Option(
                            String.format("build %s \t %s", allBuildNum.get(i), timeList.get(i)),
                            allBuildNum.get(i) + "", true));
                } else {
                    items.add(new Option(
                            String.format("build %s \t %s", allBuildNum.get(i), timeList.get(i)),
                            allBuildNum.get(i) + "", false));
                }
            }
            return items;
        }

        /**
         * Fill data in Choose Type of Diff dropdown
         * 
         * @return the ListBoxModel object for Choose Type of Diff dropdown
         */
        public ListBoxModel doFillTypeDiffItems() {
            ListBoxModel items = new ListBoxModel();
            for (int i = 0; i < TYPE_DIFF_DISPLAY.length; i++) {
                if (i == 0) {
                    items.add(new Option(TYPE_DIFF_DISPLAY[i], TYPE_DIFF_VALUE[i], true));
                } else {
                    items.add(new Option(TYPE_DIFF_DISPLAY[i], TYPE_DIFF_VALUE[i], false));
                }
            }
            return items;
        }

        /** * {@inheritDoc} */
        @Override
        public String getDisplayName() {
            return "Diff Build Action";
        }
    }
}
