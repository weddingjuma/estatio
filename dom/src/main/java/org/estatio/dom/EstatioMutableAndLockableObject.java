/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.CssClass;
import org.apache.isis.applib.annotation.Hidden;

public abstract class EstatioMutableAndLockableObject<T extends EstatioDomainObject<T>, S extends Lockable> 
        extends EstatioMutableObject<T> 
        implements WithLockable<T,S> {

    /**
     * The status representing an unlocked - freely editable - object.
     */
    private final S statusWhenUnlocked;
    /**
     * The (single) status representing a locked (non-editable) object.
     * 
     * <p>
     * This will be <tt>null</tt> if the subclass does not support explicit locking/unlocking.
     */
    private final S statusWhenLockedIfAny;

    public EstatioMutableAndLockableObject(
            final String keyProperties, final S statusWhenUnlocked, final S statusWhenLockedIfAny) {
        super(keyProperties);
        this.statusWhenUnlocked = statusWhenUnlocked;
        this.statusWhenLockedIfAny = statusWhenLockedIfAny;
    }

    // //////////////////////////////////////
    
    public void created() {
        setLockable(statusWhenLockedIfAny);
    }
    
    // //////////////////////////////////////
    

    @Hidden
    @Override
    public boolean isLocked() {
        return getLockable()!=null? !getLockable().isUnlocked(): true;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @CssClass("lock")
    @Override
    @SuppressWarnings("unchecked")
    public T lock() {
        setLockable(statusWhenLockedIfAny);
        return (T) this;
    }

    @Override
    public boolean hideLock() {
        return cannotExplicitlyLockAndUnlock() || isLocked();
    }

    @CssClass("unlock")
    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    @SuppressWarnings("unchecked")
    public T unlock() {
        setLockable(statusWhenUnlocked);
        return (T) this;
    }
    
    @Override
    public boolean hideUnlock() {
        return cannotExplicitlyLockAndUnlock() || !isLocked();
    }

    private boolean cannotExplicitlyLockAndUnlock() {
        return statusWhenLockedIfAny == null;
    }
    
    // //////////////////////////////////////

    /**
     * Disable (for all properties)
     */
    public String disabled(final Identifier.Type type) {
        if(type == Identifier.Type.PROPERTY_OR_COLLECTION) {
            return isLocked()? "Cannot modify when locked": null;
        }
        return null;
    }


}