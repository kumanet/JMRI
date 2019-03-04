package jmri.jmrix;

import javax.annotation.Nonnull;
import jmri.spi.JmriServiceProviderInterface;

/**
 * Definition of objects to handle configuring a layout connection through a stream port.
 *
 * Implementing classes <em>must</em> be registered as service providers of this
 * type to be recognized and usable.
 * <p>
 * General design documentation is available on the 
 * <a href="http://jmri.org/help/en/html/doc/Technical/SystemStructure.shtml">Structure of External System Connections page</a>.
 *
 * @author Bob Jacobsen Copyright (C) 2001, 2003
 * @see JmrixConfigPane
 * @see ConnectionConfig
 * @see java.util.ServiceLoader
 */
public interface StreamConnectionTypeList extends ConnectionTypeList {
}
