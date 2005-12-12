/*

   Derby - Class org.apache.derby.iapi.sql.dictionary.ColPermsDescriptor

   Copyright 2005 The Apache Software Foundation or its licensors, as applicable.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.UUID;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.services.io.FormatableBitSet;

/**
 * This class describes a row in the SYS.SYSCOLPERMS system table, which keeps
 * the column permissions that have been granted but not revoked.
 */
public class ColPermsDescriptor extends PermissionsDescriptor
{
    private final UUID tableUUID;
    private final String type;
    private final FormatableBitSet columns;
	
	public ColPermsDescriptor( DataDictionary dd,
                               String grantee,
                               String grantor,
                               UUID tableUUID,
                               String type,
                               FormatableBitSet columns)
	{
		super (dd, grantee, grantor);
        this.tableUUID = tableUUID;
        this.type = type;
        this.columns = columns;
	}

    /**
     * This constructor just initializes the key fields of a ColPermsDescriptor
     */
	public ColPermsDescriptor( DataDictionary dd,
                               String grantee,
                               String grantor,
                               UUID tableUUID,
                               String type)
    {
        this( dd, grantee, grantor, tableUUID, type, (FormatableBitSet) null);
    }
    
    public int getCatalogNumber()
    {
        return DataDictionary.SYSCOLPERMS_CATALOG_NUM;
    }
	
	/*----- getter functions for rowfactory ------*/
    public UUID getTableUUID() { return tableUUID;}
    public String getType() { return type;}
    public FormatableBitSet getColumns() { return columns;}

	public String toString()
	{
		return "colPerms: grantor=" + getGrantee() + 
			",grantor=" + getGrantor() +
          ",tableUUID=" + getTableUUID() +
          ",type=" + getType() +
          ",columns=" + getColumns();
	}		

    /**
     * @return true iff the key part of this permissions descriptor equals the key part of another permissions
     *         descriptor.
     */
    public boolean equals( Object other)
    {
        if( !( other instanceof ColPermsDescriptor))
            return false;
        ColPermsDescriptor otherColPerms = (ColPermsDescriptor) other;
        return super.keyEquals( otherColPerms) &&
          tableUUID.equals( otherColPerms.tableUUID) &&
          ((type == null) ? (otherColPerms.type == null) : type.equals( otherColPerms.type));
    }
    
    /**
     * @return the hashCode for the key part of this permissions descriptor
     */
    public int hashCode()
    {
        return super.keyHashCode() + tableUUID.hashCode() +
          ((type == null) ? 0 : type.hashCode());
    }
}
