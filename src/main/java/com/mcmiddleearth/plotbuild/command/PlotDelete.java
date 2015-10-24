/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotDelete extends InsidePlotCommand {
    
    public PlotDelete(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": deletes a plot.");
        setUsageDescription(" [-k]: When inside a plot removes build perms and borders and build perms. By default also the changes made inside the plot are rolled back, however the flag [-k] can be used to keep the changes.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        String logMessage1 = " deleted ", logMessage2 = "";
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plot.getPlotbuild())) {
            return;
        }
        boolean keep=false;
        if(args.length > 0 && args[0].equalsIgnoreCase("-k")) {
            keep = true;
            sendDeleteAndKeepMessage(cs);
            for(OfflinePlayer builder: plot.getOwners()) {
                if(builder.getPlayer()!=cs) {
                    sendBuilderDeletedMessage(cs, builder, plot.getPlotbuild().getName(), plot.getID());
                }
            }
            plot.getPlotbuild().log(((Player) cs).getName()+" claimed plot "+plot.getID()+".");
        }
        else {
            sendDeleteMessage(cs);
            for(OfflinePlayer builder: plot.getOwners()) {
                if(builder.getPlayer()!=cs) {
                    sendBuilderDeletedAndClearedMessage(cs, builder, plot.getPlotbuild().getName(), plot.getID());
                }
            }
            logMessage1 =  " deleted and cleared ";
        }
        try {
            plot.delete(keep);
        } catch (InvalidRestoreDataException ex) {
            Logger.getLogger(PlotDelete.class.getName()).log(Level.SEVERE, null, ex);
            sendRestoreErrorMessage(cs);
            logMessage1 = " deleted ";
            logMessage2 = " There was an error during clearing of the plot.";
        }
        PlotBuild plotbuild = plot.getPlotbuild();
        plotbuild.log(((Player) cs).getName()+logMessage1+"plot "+plot.getID()+"."+logMessage2);
        PluginData.saveData();
        if(plotbuild.countUnfinishedPlots()==0) {
            PluginData.getConfFactory().startQuery((Player)cs, getLastPlotDeletedQuery(plotbuild), plotbuild, true);
        }
    }

    private void sendDeleteMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You deleted this plot and cleared the changes within.");
    }

    private void sendDeleteAndKeepMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You deleted this plot and kept the changes within.");
    }

    private void sendBuilderDeletedAndClearedMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, "Your plot #" + id
                                                     + " of plotbuild " + name 
                                                     + " was removed by "+ cs.getName()+".");
    }
  
    private void sendBuilderDeletedMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, "Your plot #" + id
                                                     + " of plotbuild " + name 
                                                     + " was resetted to initial state and removed by "+ cs.getName()+".");
    }
    
    private String getLastPlotDeletedQuery(PlotBuild plotbuild) {
        return "You deleted the last open plot of the plotbuild "+plotbuild.getName()+". Do you want to end this plotbuild now?";
    }
    
}
