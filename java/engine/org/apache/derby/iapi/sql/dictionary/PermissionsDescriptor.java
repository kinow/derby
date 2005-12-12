/*

   Derby - Class org.apache.derby.iapi.sql.dictionary.PermissionsDescriptor

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

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.services.sanity.SanityManager;

/**
 * This class is used by rows in the SYS.SYSTABLEPERMS, SYS.SYSCOLPERMS, and SYS.SYSROUTINEPERMS
 * system tables.
 */
public abstract class PermissionsDescriptor extends TupleDescriptor implements Cloneable
{
	protected String grantee;
	protected String grantor;

	public PermissionsDescriptor( DataDictionary dd,
								  String grantee,
								  String grantor)
	{
		super (dd);
		this.grantee = grantee;
		this.grantor = grantor;
	}

	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch( java.lang.CloneNotSupportedException cnse)
		{
			if( SanityManager.DEBUG)
				SanityManager.THROWASSERT( "Could not clone a " + getClass().getName());
			return null;
		}
	}
	
	public abstract int getCatalogNumber();

	/**
	 * @return true iff the key part of this permissions descriptor equals the key part of another permissions
	 *		 descriptor.
	 */
	protected boolean keyEquals( PermissionsDescriptor other)
	{
		return grantee.equals( other.grantee);
	}
		   
	/**
	 * @return the hashCode for the key part of this permissions descriptor
	 */
	protected int keyHashCode()
	{
		return grantee.hashCode();
	}
	
	public void setGrantee( String grantee)
	{
		this.grantee = grantee;
	}
	
	/*----- getter functions for rowfactory ------*/
	public String getGrantee() { return grantee;}
	public String getGrantor() { return grantor;}
}
