package top.heyx.kettle.listener;

import com.xxl.job.core.log.XxlJobLogger;
import lombok.Setter;
import org.pentaho.di.core.logging.*;

import java.util.List;

/**
 * @AUTHOR HZL
 * @MAIL HZL031612@gmail.com
 * @DATE 2020/3/9 20:59
 */
public class LogListenerImpl implements KettleLoggingEventListener {
    @Setter
    private String channelId;
    @Setter
    private boolean includeGeneral;
    @Override
    public void eventAdded(KettleLoggingEvent event) {
        Object payload = event.getMessage();
        if (payload instanceof LogMessage){
            LogMessage message=(LogMessage) payload;
            List<String> childIds = LoggingRegistry.getInstance().getLogChannelChildren(channelId);
            boolean include = childIds==null;
            if ( !include ) {
                LoggingObjectInterface loggingObject =
                        LoggingRegistry.getInstance().getLoggingObject( message.getLogChannelId() );

                if ( loggingObject != null
                        && includeGeneral && LoggingObjectType.GENERAL.equals( loggingObject.getObjectType() ) ) {
                    include = true;
                }

                // See if we should include a certain channel id (zero, one or more)
                //
                if ( !include ) {
                    for ( String id : childIds ) {
                        if ( message.getLogChannelId().equals( id ) ) {
                            include = true;
                            break;
                        }
                    }
                }
            }

            if ( include ) {
                try {
                    XxlJobLogger.log(KettleLogStore.getAppender().getLayout().format(event));
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
