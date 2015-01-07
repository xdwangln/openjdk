/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @library /testlibrary /testlibrary/whitebox
 * @build JVMDefineModule
 * @run main ClassFileInstaller sun.hotspot.WhiteBox
 *                              sun.hotspot.WhiteBox$WhiteBoxPermission
 * @run main/othervm -Xbootclasspath/a:. -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI JVMDefineModule
 */

import com.oracle.java.testlibrary.*;
import sun.hotspot.WhiteBox;
import static com.oracle.java.testlibrary.Asserts.*;

public class JVMDefineModule {

    public static void main(String args[]) throws Exception {
        WhiteBox wb = WhiteBox.getWhiteBox();
        MyClassLoader cl = new MyClassLoader();
        Object m;

        // NULL classloader argument, expect success
        m = wb.DefineModule("mymodule", null, new String[] { "mypackage" });
        assertNotNull(m, "Module should not be null");

        // Invalid classloader argument, expect an IAE
        try {
            wb.DefineModule("mymodule1", new Object(), new String[] { "mypackage1" });
            throw new RuntimeException("Failed to get the expected IAE");
        } catch(IllegalArgumentException e) {
            // Expected
        }

        // NULL package argument, should not throw an exception
        m = wb.DefineModule("mymodule2", cl, null);
        assertNotNull(m, "Module should not be null");

        // NULL module name, expect an NPE
        try {
            wb.DefineModule(null, cl, new String[] { "mypackage2" });
            throw new RuntimeException("Failed to get the expected NPE");
        } catch(NullPointerException e) {
            // Expected
        }

        // module name is java.base, expect an IAE
        try {
            wb.DefineModule("java.base", cl, new String[] { "mypackage3" });
            throw new RuntimeException("Failed to get the expected IAE");
        } catch(IllegalArgumentException e) {
            // Expected
        }

        // Duplicates in package list, expect an IAE
        try {
            wb.DefineModule("java.base", cl, new String[] { "mypackage4", "mypackage5", "mypackage4" });
            throw new RuntimeException("Failed to get the expected IAE");
        } catch(IllegalArgumentException e) {
            // Expected
        }

        // Empty entry in package list, expect an IAE
        try {
            wb.DefineModule("java.base", cl, new String[] { "mypackageX", "", "mypackageY" });
            throw new RuntimeException("Failed to get the expected IAE");
        } catch(IllegalArgumentException e) {
            // Expected
        }

        // Duplicate module name, expect an IAE
        m = wb.DefineModule("module.name", cl, new String[] { "mypackage6" });
        assertNotNull(m, "Module should not be null");
        try {
            wb.DefineModule("module.name", cl, new String[] { "mypackage7" });
            throw new RuntimeException("Failed to get the expected IAE");
        } catch(IllegalArgumentException e) {
            // Expected
        }

        // Package is already defined for class loader, expect an IAE
        try {
            wb.DefineModule("dupl.pkg.module", cl, new String[] { "mypackage6" });
            throw new RuntimeException("Failed to get the expected IAE");
        } catch(IllegalArgumentException e) {
            // Expected
        }

        // Empty module name, expect an IAE
        try {
            wb.DefineModule("", cl, new String[] { "mypackage8" });
            throw new RuntimeException("Failed to get the expected IAE");
        } catch(IllegalArgumentException e) {
            // Expected
        }

        // Bad module name, expect an IAE
        try {
            wb.DefineModule("bad;name", cl, new String[] { "mypackage9" });
            throw new RuntimeException("Failed to get the expected IAE");
        } catch(IllegalArgumentException e) {
            // Expected
        }

        // Zero length package list, should be okay
        m = wb.DefineModule("zero.packages", cl, new String[] { });
        assertNotNull(m, "Module should not be null");

        // Invalid package name, expect an IAE
        try {
            wb.DefineModule("module5", cl, new String[] { "your.package" });
            throw new RuntimeException("Failed to get the expected IAE");
        } catch(IllegalArgumentException e) {
            // Expected
        }

        // Invalid package name, expect an IAE
        try {
            wb.DefineModule("module6", cl, new String[] { ";your/package" });
            throw new RuntimeException("Failed to get the expected IAE");
        } catch(IllegalArgumentException e) {
            // Expected
        }

        // Invalid package name, expect an IAE
        try {
            wb.DefineModule("module7", cl, new String[] { "7[743" });
            throw new RuntimeException("Failed to get the expected IAE");
        } catch(IllegalArgumentException e) {
            // Expected
        }
    }

    static class MyClassLoader extends ClassLoader { }
}

