package org.nafai.ollplltrainer;

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

    public WebPageGenerator(AssetManager assets) {
        this.mAssets = assets;
    }

    //url = "file:///android_asset/www/oll.html";
    public String Generate(AlgClass algClass) {
        this.mDoc = "";
        AddLine("<html>");
        AddLine("<head>");
        CreateStyles();
        AddLine("</head>");
        AddLine("<body>");
        AddSVGDefs(algClass);
        CreateTable(algClass);
        AddLine("</body>");
        AddLine("</html>");

        return this.mDoc;
    }

    private void CreateStyles() {
        AddLine("<style>"+
                "body { font-family: sans-serif; }\n" +
                "table { border-collapse: collapse; }\n" +
                "td { vertical-align: middle; padding: 8px; }\n" +
                "tr { border-bottom: 1px solid #555; }\n" +
                "tr.row { background-color: #8cd; border-left: 1px solid #555; border-right: 1px solid #555; }\n" +
                "h2 { color: #8cd; padding: 18px; margin: 0; }\n" +
                "img { width:100px; }\n" +
                "span.name { font-weight: bold; }\n" +
                "span.alg { font-weight: bold; }\n" +
                "svg.icon-cube { width:100px; height:100px; }\n" +
                "</style>");
    }

    private void CreateTable(AlgClass algClass) {
        AddLine("<table>");
        AddLine("<tbody>");
        switch (algClass)
        {
            case OLL:
                for (AlgGroup algGroup : AlgDb.Instance.OLLGroups) {
                    CreateSection(algClass, algGroup);
                }
                break;
            case PLL:
                for (AlgGroup algGroup : AlgDb.Instance.PLLGroups) {
                    CreateSection(algClass, algGroup);
                }
                break;
        }
        AddLine("</tbody>");
        AddLine("</table>");
    }

    private void CreateSection(AlgClass algClass, AlgGroup algGroup) {
        AddLine("<tr class=\"section\">");
        AddLine("<td colspan=\"2\"><h2>" + algGroup.Name + "</h2></td>");
        AddLine("</tr>");
        for (String id : algGroup.Entries) {
            Alg alg = null;
            switch (algClass)
            {
                case OLL:
                    alg = AlgDb.Instance.FindAlg(AlgDb.Instance.OLLs, id);
                    break;
                case PLL:
                    alg = AlgDb.Instance.FindAlg(AlgDb.Instance.PLLs, id);
                    break;
            }
            if (alg != null) {
                CreateEntry(alg);
            }
            else {
                AddLine("<tr class=\"row\"><td colspan=\"2\"></td>"+id+" not found</tr>");
            }
        }
    }

    private void CreateEntry(Alg alg) {
        AddLine("<tr class=\"row\">");
        AddLine("<td><svg class=\"icon icon-cube\"><use xlink:href=\"#"+alg.Id+"\"></use></svg></td>");
        AddLine("<td>");
        AddLine("<span class=\"name\">"+ alg.Id + ". "+alg.Names+"</span><br/>");
        AddLine("<span class=\"alg\">"+alg.Entries.get(0)+"</span><br/>");
        AddLine("</td>");
        AddLine("</tr>");
    }

    private void AddLine(String string) {
        this.mDoc += string;
    }

    private void AddSVGDefs(AlgClass algClass) {
        ArrayList<String> ids = null;
        String assetName = "";
        switch (algClass)
        {
            case OLL:
                ids = AlgDb.Instance.OLLIds;
                assetName = "oll_";
                break;
            case PLL:
                ids = AlgDb.Instance.OLLIds;
                assetName = "pll_";
                break;
        }

        if (ids != null) {
            AddLine("<svg style=\"position: absolute; width: 0; height: 0; overflow: hidden\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "<defs>\n");
            for (String id : ids) {
                String asset = GetAsset( assetName + id + ".svg");
                if (asset != null) {
                    AddLine("<symbol id=\"" + id +"\" viewBox=\"0 0 210 210\">\n");
                    AddLine(asset);
                    AddLine("</symbol>\n");
                }
            }
            AddLine("</defs>\n" +
                "</svg>\n");
        }
    }

    private String GetAsset(String fileName) {
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
