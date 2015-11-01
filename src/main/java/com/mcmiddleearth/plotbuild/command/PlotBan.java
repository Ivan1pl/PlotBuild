/* 
 *  Copyright (C) 2015 Minecraft Middle Earth
 * 
 *  This file is part of PlotBuild.
 * 
 *  PlotBuild is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PlotBuild is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PlotBuild.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotBan extends PlotBuildCommand {
    
    public PlotBan(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Bans a player from a plotbuild.");
        setUsageDescription(" <player> [name]: Restricts player from claiming any further plots in the plotbuild [name]. If [name] is not specified, current plotbuild is used. Removes the player from all plots. Does not clear plots.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        String logMessage = "";
        PlotBuild plotbuild = checkPlotBuild((Player) cs, 1, args);
        if(plotbuild == null) {
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plotbuild)) {
            return;
        }
        OfflinePlayer banned = Bukkit.getOfflinePlayer(args[0]);
        if(banned.getLastPlayed()==0) {
            sendPlayerNotFoundMessage(cs);
            return;
        }
        if(plotbuild.isBanned(banned)) {
            sendPlayerAlreadyBannedMessage(cs, banned.getName(), plotbuild.getName());
            return;
        }
        for(Plot plot : plotbuild.getPlots()) {
            if(plot.getState()!=PlotState.REMOVED && plot.isOwner(banned)) {
                if(plot.countOwners()==1) {
                    try {
                        plot.unclaim();
                    } catch (InvalidRestoreDataException ex) {
                        Logger.getLogger(PlotDelete.class.getName()).log(Level.SEVERE, null, ex);
                        sendRestoreErrorMessage(cs);
                        logMessage = " There was an error during clearing of a plot.";
                    }
                }
                else {
                    plot.leave(banned);
                    for(OfflinePlayer builder: plot.getOfflineOwners()) {
                        if(builder.getPlayer()!=cs) {
                            sendOtherBuilderMessage(cs, builder, banned, plot.getPlotbuild().getName(), plot.getID());
                }
            }
                }
            }
        }
        plotbuild.addBanned(banned);
        if(plotbuild.isStaff(banned)) {
            plotbuild.removeStaff(banned);
            for(OfflinePlayer staff: plotbuild.getOfflineStaffList()) {
                if(staff.getPlayer()!=(Player) cs) {
                    sendOtherStaffMessage(cs, staff, banned, plotbuild.getName());
                }
            }
        }
        sendBannedMessage(cs,banned.getName(), plotbuild.getName());
        sendBannedPlayerMessage(cs, banned, plotbuild.getName());
        plotbuild.log(((Player) cs).getName()+" banned "+banned.getName()+"."+logMessage);
        PluginData.saveData();
    }

    private void sendBannedMessage(CommandSender cs, String name, String plotbuild) {
        MessageUtil.sendInfoMessage(cs, "You banned "+ name+" from plotbuild "+plotbuild + ".");
    }
    
    private void sendPlayerAlreadyBannedMessage(CommandSender cs, String name, String plotbuild) {
        MessageUtil.sendInfoMessage(cs, name+" is already banned from plotbuild "+plotbuild + ".");
    }

    private void sendBannedPlayerMessage(CommandSender cs, OfflinePlayer banned, String name) {
        MessageUtil.sendOfflineMessage(banned, "You have been excluded"
                                                     + " from plotbuild " + name 
                                                     + " by "+ cs.getName()+". Please ask staff for more info.");
    }

    private void sendOtherBuilderMessage(CommandSender cs, OfflinePlayer builder, OfflinePlayer banned, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, banned.getName()+" was removed from the build team of plot #"+id
                                                     + " of plotbuild " + name 
                                                     + " as he was banned by "+ cs.getName()+".");
    }

    private void sendOtherStaffMessage(CommandSender cs, OfflinePlayer staff, OfflinePlayer banned, String name) {
        MessageUtil.sendOfflineMessage(staff, cs.getName()+" removed " + banned.getName()+ " from staff"
                                                     + " of plotbuild " + name +" and banned him.");
    }

}
