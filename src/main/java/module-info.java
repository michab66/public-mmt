/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */

module app.mmt {
    requires framework.smack_jfx;
    requires framework.smack;
    requires java.desktop;
    requires java.logging;
    requires javafx.swing;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;

    exports de.michab.app.mmt
    to javafx.graphics;
    exports de.michab.app.mmt.components
    to javafx.fxml;

    // Open the necessary packages for resource injection.
    opens de.michab.app.mmt to framework.smack;
    opens de.michab.app.mmt.components to framework.smack, javafx.fxml;
    opens de.michab.app.mmt.lab to framework.smack;
    opens de.michab.app.mmt.screens to framework.smack;
    opens de.michab.app.mmt.screens.sdk to framework.smack;
    opens de.michab.app.mmt.reports to framework.smack;
    opens de.michab.app.mmt.util to framework.smack;
}
