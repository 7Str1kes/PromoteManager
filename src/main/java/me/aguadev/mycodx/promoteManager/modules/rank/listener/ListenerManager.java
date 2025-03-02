package me.aguadev.mycodx.promoteManager.modules.rank.listener;

import me.aguadev.mycodx.promoteManager.Promote;
import me.aguadev.mycodx.promoteManager.modules.rank.listener.list.PermissionListener;
import me.aguadev.mycodx.promoteManager.modules.rank.listener.list.UpdateRankListener;
import me.aguadev.mycodx.promoteManager.modules.provider.Manager;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class ListenerManager extends Manager {

    public ListenerManager(Promote main) {
        super(main);

        registerEvent(listeners.toArray(new Listener[0]));
    }

    public List<Listener> listeners = Arrays.asList(
            new UpdateRankListener(getMain()),
            new PermissionListener(getMain())
    );

    private void registerEvent(Listener ... listeners) {
        for (Listener listener : listeners) {
            registerListener(listener);
        }
    }
}
