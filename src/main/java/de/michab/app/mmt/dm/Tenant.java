/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.dm;

import java.util.logging.Logger;

import org.smack.util.StringUtil;

import javafx.util.StringConverter;

/**
 * The company using the software.
 *
 * @author Michael Binz
 */
public class Tenant
{
    private static final Logger LOG =
            Logger.getLogger( Tenant.class.getName() );

    private int _id;

    private String _name =
            StringUtil.EMPTY_STRING;
    private String _address1 =
            StringUtil.EMPTY_STRING;
    private String _address2 =
            StringUtil.EMPTY_STRING;

    /**
     *
     */
    public Tenant()
    {
    }

    public int getId()
    {
        return _id;
    }
    public void setId( int id )
    {
        _id = id;
    }

    public String getName()
    {
        return _name;
    }
    public void setName( String name )
    {
        if ( name != null )
            name = name.trim();

        if ( StringUtil.isEmpty( name ) )
            name = StringUtil.EMPTY_STRING;

        _name = name;
    }

    public String getAddress1()
    {
        return _address1;
    }

    public void setAddress1( String address )
    {
        if ( address != null )
            address = address.trim();

        if ( StringUtil.isEmpty( address ) )
            _address1 = StringUtil.EMPTY_STRING;

        _address1 = address;
    }

    public String getAddress2()
    {
        return _address2;
    }

    public void setAddress2( String address )
    {
        if ( address != null )
            address = address.trim();

        if ( StringUtil.isEmpty( address ) )
            _address2 = StringUtil.EMPTY_STRING;

        _address2 = address;
    }

    public final static StringConverter<Tenant> TENANT_CONVERTER =
            new StringConverter<Tenant>()
            {
                @Override
                public String toString( Tenant newValue )
                {
                    if ( newValue == null )
                        return StringUtil.EMPTY_STRING;

                    // Validate...
                    {
                        boolean hasName =
                                StringUtil.hasContent( newValue.getName() );
                        boolean hasAddr1 =
                                StringUtil.hasContent( newValue.getAddress1() );

                        if ( ! hasName || ! hasAddr1 )
                            throw new IllegalArgumentException( "Name, Addr1 have to be set in Tenant." );
                    }

                    {
                        StringBuilder forAps = new StringBuilder();
                        forAps.append( StringUtil.quote( newValue.getName() ) );
                        forAps.append( " " );
                        forAps.append( StringUtil.quote( newValue.getAddress1() ) );
                        forAps.append( " " );
                        forAps.append( StringUtil.quote( newValue.getAddress2() ) );

                        return forAps.toString();
                    }
                }

                @Override
                public Tenant fromString( String fromAps )
                {
                    if ( StringUtil.isEmpty( fromAps ) )
                        return null;

                    // Create a tenant object.
                    String[] tenantParts = StringUtil.splitQuoted( fromAps );
                    if ( tenantParts.length != 3 )
                    {
                        // Aps contains gobbledigoog.
                        LOG.warning( fromAps );
                        // Give up.
                        return null;
                    }

                    Tenant result = new Tenant();

                    result.setName( tenantParts[0] );
                    result.setAddress1( tenantParts[1] );
                    result.setAddress2( tenantParts[2] );

                    return result;
                }
            };
}
