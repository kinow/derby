/*

   Derby - Class org.apache.derby.iapi.sql.dictionary.StatementTablePermission

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

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.conn.Authorizer;
import org.apache.derby.iapi.reference.SQLState;
import org.apache.derby.iapi.store.access.TransactionController;

/**
 * This class describes a table permission used (required) by a statement.
 */

public class StatementTablePermission extends StatementPermission
{
	protected UUID tableUUID;
	protected int privType; // One of Authorizer.SELECT_PRIV, UPDATE_PRIV, etc.

	public StatementTablePermission( UUID tableUUID, int privType)
	{
		this.tableUUID = tableUUID;
		this.privType = privType;
	}

	public int getPrivType()
	{
		return privType;
	}

	public UUID getTableUUID()
	{
		return tableUUID;
	}

	public boolean equals( Object obj)
	{
		if( obj == null)
			return false;
		if( getClass().equals( obj.getClass()))
		{
			StatementTablePermission other = (StatementTablePermission) obj;
			return privType == other.privType && tableUUID.equals( other.tableUUID);
		}
		return false;
	} // end of equals

	public int hashCode()
	{
		return privType + tableUUID.hashCode();
	}
	
	/**
	 * @param tc the TransactionController
	 * @param dd A DataDictionary
	 * @param authorizationId A user
	 * @param forGrant
	 *
	 * @exception StandardException if the permission has not been granted
	 */
	public void check( TransactionController tc,
					   DataDictionary dd,
					   String authorizationId,
					   boolean forGrant)
		throws StandardException
	{
		if( ! hasPermissionOnTable( dd, authorizationId, forGrant))
		{
			TableDescriptor td = getTableDescriptor( dd);
			throw StandardException.newException( forGrant ? SQLState.AUTH_NO_TABLE_PERMISSION_FOR_GRANT
												  : SQLState.AUTH_NO_TABLE_PERMISSION,
												  authorizationId,
												  getPrivName(),
												  td.getSchemaName(),
												  td.getName());
		}
	} // end of check

	protected TableDescriptor getTableDescriptor( DataDictionary dd)  throws StandardException
	{
		TableDescriptor td = dd.getTableDescriptor( tableUUID);
		if( td == null)
			throw StandardException.newException( SQLState.AUTH_INTERNAL_BAD_UUID, "table");
		return td;
	} // end of getTableDescriptor

	protected boolean hasPermissionOnTable( DataDictionary dd, String authorizationId, boolean forGrant)
		throws StandardException
	{
		return oneAuthHasPermissionOnTable( dd, Authorizer.PUBLIC_AUTHORIZATION_ID, forGrant)
		  || oneAuthHasPermissionOnTable( dd, authorizationId, forGrant);
	}

	private boolean oneAuthHasPermissionOnTable( DataDictionary dd, String authorizationId, boolean forGrant)
		throws StandardException
	{
		TablePermsDescriptor perms = dd.getTablePermissions( tableUUID, authorizationId);
		if( perms == null)
			return false;
		
		String priv = null;
			
		switch( privType)
		{
		case Authorizer.SELECT_PRIV:
			priv = perms.getSelectPriv();
			break;
		case Authorizer.UPDATE_PRIV:
			priv = perms.getUpdatePriv();
			break;
		case Authorizer.REFERENCES_PRIV:
			priv = perms.getReferencesPriv();
			break;
		case Authorizer.INSERT_PRIV:
			priv = perms.getInsertPriv();
			break;
		case Authorizer.DELETE_PRIV:
			priv = perms.getDeletePriv();
			break;
		case Authorizer.TRIGGER_PRIV:
			priv = perms.getTriggerPriv();
			break;
		}

		return "Y".equals(priv) || (!forGrant) && "y".equals( priv);
	} // end of hasPermissionOnTable

	public String getPrivName( )
	{
		switch( privType)
		{
		case Authorizer.SELECT_PRIV:
			return "select";
		case Authorizer.UPDATE_PRIV:
			return "update";
		case Authorizer.REFERENCES_PRIV:
			return "references";
		case Authorizer.INSERT_PRIV:
			return "insert";
		case Authorizer.DELETE_PRIV:
			return "delete";
		case Authorizer.TRIGGER_PRIV:
			return "trigger";
		}
		return "?";
	} // end of getPrivName
}
