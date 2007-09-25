/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.intellij.stripes.util;

import com.intellij.psi.PsiClass;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 2/07/2007 Time: 02:04:03 AM
 */
public class StripesUtil
{
    private StripesUtil()
    {
    }

    public static boolean isSubclass(PsiClass clazz, String superClass)
    {
        if (clazz == null)
        {
            return false;
        }
        boolean b = false;
        try
        {
            b = clazz.getQualifiedName().equals(superClass);
        }
        catch (NullPointerException e)
        {
            //
        }
        if (b)
        {
            return true;
        }
        else
        {
            if (isSubclass(clazz.getSupers(), superClass))
            {
                return true;
            }
            else if (isSubclass(clazz.getInterfaces(), superClass))
            {
                return true;
            }

        }
        return false;
    }

    protected static boolean isSubclass(PsiClass[] supers, String superClass)
    {
        for (PsiClass aSuper : supers)
        {
            if (isSubclass(aSuper, superClass))
            {
                return true;
            }
        }
        return false;
    }

    public static String[] getStringsArray(String... strings)
    {
        return strings;       
    }

    public static<T> T[] makeArray(T... strings)
    {
        return strings;       
    }
}
