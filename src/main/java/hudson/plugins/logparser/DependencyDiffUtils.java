package hudson.plugins.logparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * DependencyDiffUtils is used to parse the pom file, generate the diffed
 * result, and convert the result to html page.
 */
public class DependencyDiffUtils {
    /**
     * generate the diffed result
     * 
     * @param deplist1
     *            dependencies of build1's pom
     * @param deplist2
     *            dependencies of build2's pom
     * @return a map that contains three types of diffed result: added
     *         dependency, delete dependency, and modified dependency
     */
    public static Map<String, ArrayList<Dependency>> diff(ArrayList<Dependency> deplist1,
            ArrayList<Dependency> deplist2) {
        ArrayList<Dependency> dellist = new ArrayList<Dependency>();
        ArrayList<Dependency> addlist = new ArrayList<Dependency>();
        ArrayList<Dependency> modlist = new ArrayList<Dependency>();
        Map<String, ArrayList<Dependency>> retlist = new HashMap<String, ArrayList<Dependency>>();
        for (int i = 0; i < deplist2.size(); ++i) {
            Dependency dep2 = deplist2.get(i);
            String gid2 = dep2.getGroupId();
            String aid2 = dep2.getArtifactId();
            String ver2 = dep2.getVersion();
            boolean isinlist1 = false;
            for (Dependency dep1 : deplist1) {
                String gid1 = dep1.getGroupId();
                String aid1 = dep1.getArtifactId();
                String ver1 = dep1.getVersion();
                if (gid1.equals(gid2) && aid1.equals(aid2)) {
                    isinlist1 = true;
                    if (!ver1.equals(ver2))
                        modlist.add(new Dependency(gid2, aid2, ver2));
                }
            }
            if (!isinlist1)
                addlist.add(new Dependency(gid2, aid2, ver2));
        }
        retlist.put("Modified", modlist);
        retlist.put("Added", addlist);
        for (int i = 0; i < deplist1.size(); ++i) {
            Dependency dep1 = deplist1.get(i);
            String gid1 = dep1.getGroupId();
            String aid1 = dep1.getArtifactId();
            String ver1 = dep1.getVersion();
            boolean isinlist2 = false;
            for (Dependency dep2 : deplist2) {
                String gid2 = dep2.getGroupId();
                String aid2 = dep2.getArtifactId();
                if (gid2.equals(gid1) && aid2.equals(aid1)) {
                    isinlist2 = true;
                }
            }
            if (!isinlist2)
                dellist.add(new Dependency(gid1, aid1, ver1));

        }
        retlist.put("Deleted", dellist);
        return retlist;
    }

    private static StringBuilder setupCSS() {
        final String htmlHeading = "<!DOCTYPE html> <html>\n <head>\n <title></title>\n";
        final String style = "<style type=\"text/css\">\n";
        final String leftStyle = ".left { width: 30%; float: left; clear: right; background-color: #BCF5A9; }\n";
        final String centerStyle = ".center { width: 30%; float: left; clear: right; background-color: #81DAF5; }\n";
        final String rightStyle = ".right { width: 30%; float: left; clear: right; background-color: #FA5858; }\n";
        final String bothStyle = ".both { width: 100%; clear: both; background-color: #696969; }\n";
        final String endStyle = "</style>\n";
        final String endHead = "</head>\n";
        StringBuilder sb = new StringBuilder();
        return sb.append(htmlHeading).append(style).append(leftStyle).append(centerStyle)
                .append(rightStyle).append(bothStyle).append(endStyle).append(endHead);
    }

    private static String getPrevVersion(ArrayList<Dependency> deplist, String groupId,
            String artifactId) {
        for (Dependency dep : deplist) {
            if (dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId))
                return dep.getVersion();
        }
        return "0";
    }

    /**
     * generate an html page to display the diffed result
     *
     * @param deplist1
     *            dependencies of build1's pom
     * @param deplist2
     *            dependencies of build2's pom
     * @param list
     *            diffed result
     * @param prevBuild
     *            the previous build
     * @param currBuild
     *            the current build
     * @return the html page to display the diffed result
     */
    public static String toHtml(ArrayList<Dependency> deplist1, ArrayList<Dependency> deplist2,
            Map<String, ArrayList<Dependency>> list, int prevBuildNumber, int currBuildNumber) {
        ArrayList<Dependency> modified = list.get("Modified");
        ArrayList<Dependency> added = list.get("Added");
        ArrayList<Dependency> deleted = list.get("Deleted");
        StringBuilder html = setupCSS();

        html.append("<body>\n");
        html.append("<div>\n");
        html.append("<div><p><b><font size=\"3\">Comparing the current build #" + currBuildNumber
                + " and build #" + prevBuildNumber + "</font></b></p>");
        html.append("<div class=\"left\">\n");
        html.append("<b>Dependency modified:</b><br />\n");
        for (Dependency dep : modified) {
            html.append("<br> groupId: " + dep.getGroupId() + "</br>\n");
            html.append("<br> artifactId: " + dep.getArtifactId() + "</br>\n");
            html.append("<br> build #" + currBuildNumber + " dependency version: "
                    + dep.getVersion() + "</br>\n");
            html.append("<br> build #" + prevBuildNumber + " dependency version: "
                    + getPrevVersion(deplist1, dep.getGroupId(), dep.getArtifactId()));
            html.append("<hr>\n<br />\n");
        }
        html.append("</div>\n");
        html.append("<div class=\"center\">\n");
        html.append("<b>Dependency added to build #" + currBuildNumber + "</b><br />\n");
        for (Dependency dep : added) {
            html.append("<br> groupId: " + dep.getGroupId() + "</br>\n");
            html.append("<br> artifactId: " + dep.getArtifactId() + "</br>\n");
            html.append("<br> version: " + dep.getVersion() + "</br>\n");
            html.append("<hr>\n<br />\n");
        }
        html.append("</div>\n");
        html.append("<div class=\"right\">\n");
        html.append("<b>Dependency deleted from build #" + prevBuildNumber + "</b><br />\n");
        for (Dependency dep : deleted) {
            html.append("<br> groupId: " + dep.getGroupId() + "</br>\n");
            html.append("<br> artifactId: " + dep.getArtifactId() + "</br>\n");
            html.append("<br> version: " + dep.getVersion() + "</br>\n");
            html.append("<hr>\n<br />\n");
        }
        html.append("</div>\n");
        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>");
        return html.toString();
    }

    /**
     * parse the given pom and get all dependencies
     * 
     * @param pom
     *            the inputstream of pom
     * @return the list that contains all dependencies
     */
    public static ArrayList<Dependency> parsePom(InputStream pom)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document doc = null;
        doc = dbBuilder.parse(pom);
        NodeList list = doc.getElementsByTagName("dependency");
        ArrayList<Dependency> alist = new ArrayList<Dependency>();
        for (int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);
            String groupId = element.getElementsByTagName("groupId").item(0).getFirstChild()
                    .getNodeValue();
            String artifactId = element.getElementsByTagName("artifactId").item(0).getFirstChild()
                    .getNodeValue();
            String version = element.getElementsByTagName("version").item(0).getFirstChild()
                    .getNodeValue();
            alist.add(new Dependency(groupId, artifactId, version));

        }
        return alist;
    }
}
