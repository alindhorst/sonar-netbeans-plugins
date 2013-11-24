package de.alexanderlindhorst.sonarcheckstyle.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.openide.windows.OnShowing;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.alexanderlindhorst.sonarcheckstyle.plugin.gui.FileWatch;
import de.alexanderlindhorst.sonarcheckstyle.plugin.gui.SonarCheckstyleDocumentGuiHelper;

import static de.alexanderlindhorst.sonarcheckstyle.plugin.gui.OpenJavaSourceRegistry.isKnownTopComponent;
import static de.alexanderlindhorst.sonarcheckstyle.plugin.util.SonarCheckstylePluginUtils.getUnderlyingFile;
import static de.alexanderlindhorst.sonarcheckstyle.plugin.util.SonarCheckstylePluginUtils.isJavaTopComponent;

/**
 * Hooks itself up with WindowManager upon module start and registers listeners. Thus, will be notified of any change in
 * open windows.
 */
@OnShowing
public class TopComponentsWatch implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopComponentsWatch.class);
    private static final TopComponentPropertyChangeListener LISTENER = new TopComponentPropertyChangeListener();
    private static final SonarCheckstyleDocumentGuiHelper GUI_HELPER = SonarCheckstyleDocumentGuiHelper.getDefault();
    private static final FileWatch FILE_WATCH = new FileWatch();

    /**
     * Hooks up the listener with the registry in a different thread
     */
    @Override
    public void run() {
        LOGGER.info("Attaching PropertyChangeListener to window registry to listen to newly opened files");
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(LISTENER);
        LOGGER.debug("Successfully attached PropertyChangeListener to window registry");
    }

    private static class TopComponentPropertyChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            EventMode eventMode = EventMode.findByEventNameValue(evt.getPropertyName());
            switch (eventMode) {
                case ACTIVATED:
                case OPEN:
                    hookUpComponent(evt);
                    break;
                case CLOSE:
                    releaseComponent(evt);
                    break;
                case UNSUPPORTED:
                    LOGGER.debug("event mode not supported: {}", evt.getPropertyName());
                    break;
                default:
                    throw new AssertionError();
            }
        }

        private void hookUpComponent(PropertyChangeEvent evt) {
            LOGGER.debug("hookUpComponent called for {}", ((TopComponent) evt.getNewValue()).getDisplayName());
            TopComponent topComponent = (TopComponent) evt.getNewValue();
            if (isJavaTopComponent(topComponent) && !isKnownTopComponent(topComponent)) {
                getUnderlyingFile(topComponent).addFileChangeListener(FILE_WATCH);
                GUI_HELPER.processAnnotationsFor(topComponent);
            } else {
                LOGGER.debug("Hook up not applicable to {}", topComponent.getDisplayName());
            }
        }

        private void releaseComponent(PropertyChangeEvent evt) {
            LOGGER.debug("releaseComponent called for {}", ((TopComponent) evt.getNewValue()).getDisplayName());
            TopComponent topComponent = (TopComponent) evt.getNewValue();
            if (isJavaTopComponent(topComponent) && isKnownTopComponent(topComponent)) {
                getUnderlyingFile(topComponent).removeFileChangeListener(FILE_WATCH);
                GUI_HELPER.removeAnnotationsFor(topComponent);
            } else {
                LOGGER.debug("releaseComponent not applicable to {}", topComponent.getDisplayName());
            }
        }
    }

    private static enum EventMode {

        OPEN(TopComponent.Registry.PROP_TC_OPENED),
        ACTIVATED(TopComponent.Registry.PROP_ACTIVATED),
        CLOSE(TopComponent.Registry.PROP_TC_CLOSED),
        UNSUPPORTED("");
        private final String eventName;

        private EventMode(String eventName) {
            this.eventName = eventName;
        }

        private static EventMode findByEventNameValue(String value) {
            for (EventMode eventMode : values()) {
                if (value.equals(eventMode.eventName)) {
                    return eventMode;
                }
            }
            return UNSUPPORTED;
        }
    }
}
