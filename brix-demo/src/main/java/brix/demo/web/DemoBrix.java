package brix.demo.web;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brix.Brix;
import brix.auth.AuthorizationStrategy;
import brix.config.BrixConfig;
import brix.demo.web.tile.TimeTile;
import brix.jcr.JcrSessionFactory;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.node.tilepage.TileNodePlugin;
import brix.plugin.site.node.tilepage.TilePageNode;
import brix.plugin.site.node.tilepage.TilePageNodePlugin;
import brix.plugin.site.node.tilepage.TileTemplateNode;
import brix.plugin.site.node.tilepage.TileTemplateNodePlugin;
import brix.web.tile.menu.MenuTile;
import brix.web.tile.pagetile.PageTile;
import brix.workspace.AbstractWorkspaceManager;
import brix.workspace.WorkspaceManager;
import brix.workspace.rmi.ClientWorkspaceManager;


public class DemoBrix extends Brix
{

    private static Logger logger = LoggerFactory.getLogger(DemoBrix.class);

    public DemoBrix(JcrSessionFactory sf)
    {
        super(new BrixConfig(), sf);
        TileNodePlugin plugin = new TilePageNodePlugin();
        addTiles(plugin);

        SitePlugin sitePlugin = SitePlugin.get(this);
        sitePlugin.registerNodePlugin(plugin);

        plugin = new TileTemplateNodePlugin();
        addTiles(plugin);
        sitePlugin.registerNodePlugin(plugin);

        getWrapperRegistry().registerWrapper(TilePageNode.class);
        getWrapperRegistry().registerWrapper(TileTemplateNode.class);

    }

    @Override
    protected WorkspaceManager newWorkspaceManager()
    {
        if (WicketApplication.USE_RMI)
        {
            return new ClientWorkspaceManager("rmi://localhost:1099/jackrabbitwm");
        }
        else
        {
            AbstractWorkspaceManager manager = new AbstractWorkspaceManager()
            {

                @Override
                protected void createWorkspace(String workspaceName)
                {
                    JcrSession session = getSession(null);
                    DemoBrix.this.createWorkspace(session, workspaceName);
                }

                @Override
                protected List<String> getAccessibleWorkspaceNames()
                {
                    return Arrays.asList(getSession(null).getWorkspace()
                        .getAccessibleWorkspaceNames());
                }

                @Override
                protected JcrSession getSession(String workspaceName)
                {
                    return getCurrentSession(workspaceName);
                }

            };
            manager.initialize();
            return manager;
        }
    }

    private void addTiles(TileNodePlugin plugin)
    {
        plugin.addTile(new TimeTile());
        plugin.addTile(new MenuTile());
        plugin.addTile(new PageTile());

        /*
         * plugin.addTile(new TreeMenuTile()); plugin.addTile(new LinkTile()); plugin.addTile(new
         * StatelessLinkTile()); plugin.addTile(new StatelessFormTile());
         * 
         */
    }

    @Override
    public AuthorizationStrategy newAuthorizationStrategy()
    {
        return new DemoAuthorizationStrategy();
    }
}
