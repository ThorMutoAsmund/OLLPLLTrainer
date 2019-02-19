package org.nafai.ollplltrainer;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by thora_000 on 19/12/2017.
 */

public class WebPageGenerator {
    private String mDoc;

    private AssetManager mAssets;

    private Prefs mPrefs;

    private boolean mIsEditMode;

    private int mRenderSize;

    public static String EditColor = "pink";

    public WebPageGenerator(AssetManager assets) {
        this.mAssets = assets;
    }

    //url = "file:///android_asset/www/oll.html";
    public String generate(AlgClass algClass, boolean isEditMode, int renderSize, Context context) {
        this.mPrefs = new Prefs(context);

        this.mDoc = "";
        this.mIsEditMode = isEditMode;
        this.mRenderSize = renderSize;

        addLine("<html>");
        addLine("<head>");
        createStyles();
        addLine("</head>");
        addLine("<body>");
        addSVGDefs(algClass);
        createHeader();
        createTable(algClass);
        addLine("</body>");
        addLine("</html>");

        return this.mDoc;
    }


    public String generateTrainingItem(AlgClass algClass, TrainingItem item, Context context) {
        this.mPrefs = new Prefs(context);

        this.mDoc = "";

        addLine("<html>");
        addLine("<head>");
        createStyles();
        addLine("</head>");
        addLine("<body>");
        addSVGDefs(algClass);
        createHeader();
        createTrainingItemTable(algClass, item);
        addLine("</body>");
        addLine("</html>");

        return this.mDoc;
    }

    public String generateTrainingItemSolution(AlgClass algClass, TrainingItem item, Context context) {
        this.mPrefs = new Prefs(context);

        this.mDoc = "";

        addLine("<html>");
        addLine("<head>");
        createStyles();
        addLine("</head>");
        addLine("<body>");
        addSVGDefs(algClass);
        createHeader();
        createTrainingItemSolutionTable(algClass, item);
        addLine("</body>");
        addLine("</html>");

        return this.mDoc;
    }

    private void createStyles() {
        addLine("<style>"+
                "body { font-family: sans-serif; }\n" +
                "table { border-collapse: collapse; }\n" +
                "td { vertical-align: middle; padding: 8px; }\n" +
                "tr { border-bottom: 1px solid #555; }\n" +
                "tr.row { background-color: #8cd; border-left: 1px solid #555; border-right: 1px solid #555; }\n" +
                "tr.row.level1 { background-color: #cc8 }\n" +
                "tr.row.level2 { background-color: #8d8 }\n" +
                "h2 { color: #8cd; margin: 0; }\n" +
                "span.name { font-weight: bold; }\n" +
                "span.alg { font-weight: bold; }\n" +
                ".rendersize0 svg.icon-cube { width:100px; height:100px; }\n" +
                ".rendersize1 svg.icon-cube { width:80px; height:80px; }\n" +
                ".rendersize2 svg.icon-cube { width:60px; height:60px; }\n" +
                ".rendersize0 h2 { font-size:22px; padding: 18px; }\n" +
                ".rendersize1 h2 { font-size:21px; padding: 16px; }\n" +
                ".rendersize2 h2 { font-size:20px; padding: 14px; }\n" +
                "</style>");
    }

    private void createHeader() {
        //looks ugly, better formatting?
        //addLine("<p>Click an alg to mark it completed/uncompleted</p>");
    }

    private void createTable(AlgClass algClass) {
        String backgroundColor = this.mIsEditMode ? EditColor : "";
        addLine("<table class=\"rendersize"+this.mRenderSize+"\" style=\"background-color: "+backgroundColor+"\" id =\"algTable\" width=\"100%\">");
        addLine("<tbody>");
        switch (algClass)
        {
            case OLL:
                for (AlgGroup algGroup : AlgDb.Instance.OLLGroups) {
                    createSection(algClass, algGroup);
                }
                break;
            case PLL:
                for (AlgGroup algGroup : AlgDb.Instance.PLLGroups) {
                    createSection(algClass, algGroup);
                }
                break;
        }
        addLine("</tbody>");
        addLine("</table>");
    }

    private void createTrainingItemTable(AlgClass algClass, TrainingItem item) {
        String backgroundColor = this.mIsEditMode ? EditColor : "";
        addLine("<table class=\"rendersize"+this.mRenderSize+"\" style=\"background-color: "+backgroundColor+"\" id =\"algTable\" width=\"100%\">");
        addLine("<tbody>");
        addLine("<tr class=\"row\">");

        for (String id : item.IdsOfAlgsToPerform) {
            createTrainingItemImage(algClass, id);
        }

        addLine("<td width=\"\100%\"></td>");
        addLine("</tr>");
        addLine("</tbody>");
        addLine("</table>");
    }

    private void createTrainingItemSolutionTable(AlgClass algClass, TrainingItem item) {
        String backgroundColor = this.mIsEditMode ? EditColor : "";
        addLine("<table class=\"rendersize"+this.mRenderSize+"\" style=\"background-color: "+backgroundColor+"\" id =\"algTable\" width=\"100%\">");
        addLine("<tbody>");
        addLine("<tr class=\"row\">");

        Alg alg = AlgDb.Instance.findAlg(algClass, item.IdOfAlgToPractice);
        createTrainingItemSolution(algClass, alg);

        addLine("<td width=\"\100%\"></td>");
        addLine("</tr>");
        addLine("</tbody>");
        addLine("</table>");
    }

    private void createSection(AlgClass algClass, AlgGroup algGroup) {
        addLine("<tr class=\"section\">");
        addLine("<td colspan=\"2\"><h2>" + algGroup.Name + "</h2></td>");
        addLine("</tr>");
        for (String id : algGroup.Entries) {
            Alg alg = AlgDb.Instance.findAlg(algClass, id);

            if (alg != null) {
                createEntry(algClass, alg);
            }
            else {
                addLine("<tr class=\"row\"><td colspan=\"2\"></td>"+id+" not found</tr>");
            }
        }
    }

    private void createEntry(AlgClass algClass, Alg alg) {
        String entry = this.mPrefs.getEntry(algClass, alg.Id, alg.Entries.get(0));
        int rotation = this.mPrefs.getRotation(algClass, alg.Id);
        int completedLevel = this.mPrefs.getIsEntryCompleted(algClass, alg.Id);

        addLine("<tr id=\"algRow_" + alg.Id + "\" class=\"row level"+completedLevel+"\">");
        addLine("<td><svg id=\"algImage_" + alg.Id + "\" onClick=\"android.algImageClicked('"+alg.Id+"')\" class=\"icon icon-cube\" style=\"transform: rotate("+rotation+"deg)\"><use xlink:href=\"#"+alg.Id+"\"></use></svg></td>");
        addLine("<td width=\"100%\">");
        addLine("<span onClick=\"android.algClicked('" + alg.Id + "')\" class=\"name\">"+ alg.Id + ". "+alg.Names+"</span><br/>");
        addLine("<span id=\"algEntry_" + alg.Id + "\" onClick=\"android.algClicked('" + alg.Id + "')\" class=\"alg\">"+entry+"</span><br/>");
        addLine("</td>");
        addLine("</tr>");
    }

    private void createTrainingItemImage(AlgClass algClass, String algId) {
        int rotation = this.mPrefs.getRotation(algClass, algId);

        addLine("<td><svg id=\"algImage_" + algId + "\" class=\"icon icon-cube\" style=\"transform: rotate("+rotation+"deg)\"><use xlink:href=\"#"+algId+"\"></use></svg></td>");
    }

    private void createTrainingItemSolution(AlgClass algClass, Alg alg) {
        String entry = this.mPrefs.getEntry(algClass, alg.Id, alg.Entries.get(0));
        int rotation = this.mPrefs.getRotation(algClass, alg.Id);
        int completedLevel = this.mPrefs.getIsEntryCompleted(algClass, alg.Id);

        addLine("<tr id=\"algRow_" + alg.Id + "\" class=\"row\">");
        addLine("<td><svg id=\"algImage_" + alg.Id + "\" class=\"icon icon-cube\" style=\"transform: rotate("+rotation+"deg)\"><use xlink:href=\"#"+alg.Id+"\"></use></svg></td>");
        addLine("<td width=\"100%\">");
        addLine("<span class=\"name\">"+ alg.Id + ". "+alg.Names+"</span><br/>");
        addLine("<span id=\"algEntry_" + alg.Id + "\" class=\"alg\">"+entry+"</span><br/>");
        addLine("</td>");
        addLine("</tr>");
    }

    private void addLine(String string) {
        this.mDoc += string;
    }

    private void addSVGDefs(AlgClass algClass) {
        ArrayList<String> ids = null;
        String assetName = "";
        switch (algClass)
        {
            case OLL:
                ids = AlgDb.Instance.OLLIds;
                assetName = "oll_";
                break;
            case PLL:
                ids = AlgDb.Instance.PLLIds;
                assetName = "pll_";
                break;
        }

        if (ids != null) {
            addLine("<svg style=\"position: absolute; width: 0; height: 0; overflow: hidden\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "<defs>\n");
            for (String id : ids) {
                String asset = getAsset( assetName + id + ".svg");
                if (asset != null) {
                    // Remove xml and svg tags not needed when inlining
                    asset = asset.replace("<?xml version=\"1.0\"?>","");
                    asset = asset.replace("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">","");
                    asset = asset.replace("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"200\" height=\"200\" viewBox=\"0 0 200 200 \">","");
                    asset = asset.replace("</svg>","");

                    // Add svg icon as symbol
                    addLine("<symbol id=\"" + id +"\" viewBox=\"0 0 200 200\">\n");
                    addLine(asset);
                    addLine("</symbol>\n");
                }
            }
            addLine("</defs>\n" +
                "</svg>\n");
        }
    }

    private String getAsset(String fileName) {
        try
        {
            StringBuilder buf = new StringBuilder();
            InputStream json = this.mAssets.open(fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();

            return buf.toString();
        }
        catch (IOException ex) {
            return null;
        }
    }
}
