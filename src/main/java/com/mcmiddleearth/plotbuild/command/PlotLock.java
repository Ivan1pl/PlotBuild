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

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.BukkitUtil;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotLock extends PlotBuildCommand {
    
    public PlotLock(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Locks plotbuilds.");
        setUsageDescription(" [name]: Locks the plotbuild [name]. If [name] is not specified all plotbuilds are locked. This prevents non-staff to build on plots, claim new plots and invite players to plots. This can be used during jobs to get the people working on the plotbuilds to help on the job.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(args.length==0) {
            for(PlotBuild plotbuild : PluginData.getPlotbuildsList()) {
                plotbuild.setLocked(true);
                plotbuild.log(((Player) cs).getName()+" locked the plotbuild.");
            }
            sendAllLockBuilderMessages(cs);
            sendLockedAllMessage(cs);
        }
        else {
            PlotBuild plotbuild = PluginData.getPlotBuild(args[0]);
            if(plotbuild == null) {
                sendNoPlotbuildFoundMessage(cs);
                return;
            }
            plotbuild.setLocked(true);
            sendLockedPlotbuild(cs,plotbuild.getName());
            sendBuilderMessages(cs, plotbuild);
            plotbuild.log(((Player) cs).getName()+" locked the plotbuild.");
        }
        PluginData.saveData();
    }

    private void sendBuilderMessages(CommandSender cs, PlotBuild plotbuild) {
        Set<UUID> builders = plotbuild.getBuilders();
        for(UUID player : builders) {
            if(BukkitUtil.isOnline(player) && !player.equals(((Player) cs).getUniqueId())) {
                MessageUtil.sendInfoMessage(Bukkit.getPlayer(player), cs.getName() + "locked plotbuild "
                                               +plotbuild.getName()+".");
            }
        }
    }
    
    private void sendLockedAllMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You locked all plotbuilds.");
    }

    private void sendLockedPlotbuild(CommandSender cs, String name) {
        MessageUtil.sendInfoMessage(cs, "You locked the plotbuild "+name+".");
    }

    private void sendAllLockBuilderMessages(CommandSender cs) {
        Set<UUID> builders = PluginData.getBuilders();
        for(UUID player : builders) {
            if(BukkitUtil.isOnline(player) && !player.equals(((Player) cs).getUniqueId())) {
                MessageUtil.sendInfoMessage(Bukkit.getPlayer(player), cs.getName() + " locked all plotbuilds.");
            }
        }
    }

    
}
