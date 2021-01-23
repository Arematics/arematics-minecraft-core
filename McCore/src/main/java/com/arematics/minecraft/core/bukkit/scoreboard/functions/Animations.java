package com.arematics.minecraft.core.bukkit.scoreboard.functions;

public class Animations {



    /* TODO Rewrite
    static void animateValue(final Player p, String entryId, final long beginValue, final long endValue){
        final BukkitScheduler sched = Bukkit.getScheduler();

        entryId = ChatColor.stripColor(entryId.replace(":", "").toLowerCase());
        final String name = p.getUniqueId() + ":" + entryId.toLowerCase();
        if(animationTaskIds.containsKey(name)){
            for(int taskId : animationTaskIds.get(name))
                if(sched.isCurrentlyRunning(taskId) || sched.isQueued(taskId))
                    sched.cancelTask(taskId);
            animationTaskIds.remove(name);
        }
        if(BoardSet.getSet(p).getBoard("main") == null)
            createDefaultScoreboard(p);
        final Board b = BoardSet.getSet(p).getBoard("main");
        final BoardEntry be = b.getEntry(entryId);
        if(be == null) return;

        if(beginValue == endValue || beginValue == -1){
            be.setSuffix(fancify(endValue));
            return;
        }
        final ArrayList<Integer> taskIds = new ArrayList<>();
        animationTaskIds.put(name, taskIds);
        int taskId = sched.scheduleSyncDelayedTask(SPS.instance, () -> {
            final long dist = endValue - beginValue;

            int ticksDuration = 12;
            if(Math.abs(dist) < 100) ticksDuration = 5;
            if(Math.abs(dist) < 20) ticksDuration = 1;

            for(int i = 0; i < ticksDuration; i++){
                try{
                    final String newText = (dist > 0 ? "§a" : "§c")  + (ticksDuration == 1 || i == ticksDuration -1 ? fancify(endValue) : fancify(beginValue + ((dist / ticksDuration) * (i+1))));
                    int id = sched.scheduleSyncDelayedTask(SPS.instance, () -> {
                        if(!p.isOnline()) return;

                        be.setSuffix(newText);
                    }, i);
                    taskIds.add(id);
                }catch(Exception ex){} // Should not occur. But for safety.
            }

            int id = sched.scheduleSyncDelayedTask(SPS.instance, () -> {
                animationTaskIds.remove(name);
                if(!p.isOnline()) return;

                be.setSuffix(fancify(endValue));
            }, ticksDuration + 20);
            taskIds.add(id);

        });
        taskIds.add(taskId);
        animationTaskIds.put(name, taskIds);
    }
    */
}
