/*
 * This file is part of Cactus Rush, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.game;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.teams.Team;
import net.jadedmc.cactusrush.utils.DateUtils;
import net.jadedmc.jadedutils.scoreboard.CustomScoreboard;
import net.jadedmc.jadedutils.scoreboard.ScoreHelper;
import org.bukkit.entity.Player;

public class GameScoreboard extends CustomScoreboard {
    private final Game game;
    private final CactusRushPlugin plugin;

    public GameScoreboard(CactusRushPlugin plugin, Player player, Game game) {
        super(player);
        this.plugin = plugin;
        this.game = game;

        CustomScoreboard.getPlayers().put(player.getUniqueId(), this);
        update(player);
    }

    public void update(Player player) {
        ScoreHelper helper;

        if(ScoreHelper.hasScore(player)) {
            helper = ScoreHelper.getByPlayer(player);
        }
        else {
            helper = ScoreHelper.createScore(player);
        }

        switch (game.gameState()) {
            case WAITING, COUNTDOWN -> {
                helper.setTitle("&a&lCACTUS RUSH");
                helper.setSlot(10, "&7" + DateUtils.currentDateToString());
                helper.setSlot(9, "");
                helper.setSlot(8, "&fMap: &a" + game.arena().name());

                if(game.mode() != Mode.DUEL) {
                    helper.setSlot(7, "&fPlayers: &a" + game.players().size() + "&f/&a" + game.mode().maxPlayerCount());
                }
                else {
                    helper.setSlot(7, "&fPlayers: &a" + game.players().size());
                }

                helper.setSlot(6, "");
                helper.setSlot(5, "&fAbility: " + plugin.abilityManager().getAbility(player).name());
                helper.setSlot(4, "");

                if(game.gameState() == GameState.COUNTDOWN) {
                    helper.setSlot(3, "&fStarting in &a" + game.gameCountdown().seconds() +  "s");
                }
                else {
                    helper.setSlot(3, "&fWaiting for players");
                }

                helper.setSlot(2, " ");
                helper.removeSlot(1);
                helper.setSlot(1, "&aplay.jadedmc.net");
            }

            case RUNNING, BETWEEN_ROUND, END -> {
                helper.setTitle("&a&lCACTUS RUSH");
                helper.setSlot(15, "&7" + DateUtils.currentDateToString());
                helper.setSlot(14, "");
                helper.setSlot(13, "&fRound: &a" + game.round());
                helper.setSlot(12, "&fMap: &a" + game.arena().name());
                helper.setSlot(11, "");

                int slot = 10;
                for(Team team : game.teamManager().teams()) {
                    helper.setSlot(slot, team.formattedScore());
                    slot--;
                }

                helper.setSlot(8, " ");

                if(!game.spectators().contains(player)) {
                    helper.setSlot(7, "Cacti Placed: &a" + game.statisticsTracker().getRoundCactiPlaced(player));
                    helper.setSlot(6, "Eggs Thrown: &a" + game.statisticsTracker().getRoundEggsThrown(player));
                    helper.setSlot(5, " ");
                    helper.setSlot(4, "&fAbility: " + plugin.abilityManager().getAbility(player).name());
                    helper.setSlot(3, "");
                }
                else {
                    helper.removeSlot(7);
                    helper.removeSlot(6);
                    helper.removeSlot(5);
                    helper.removeSlot(4);
                    helper.removeSlot(3);
                }

                helper.removeSlot(2);
                helper.setSlot(1, "&aplay.jadedmc.net");

            }
        }
    }

}