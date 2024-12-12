/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import vista.set.DataSetAttr;

/**
 * A set of data attributes that contains original units field.
 *
 * @author Armin Munevar
 * @version $Id: TSDataAttr.java,v 1.1.2.3 2001/07/12 01:58:32 amunevar Exp $
 */

public class TSDataAttr extends DataSetAttr {
    /**
     *
     */
    public TSDataAttr(DataSetAttr attr) {
        super(attr.getGroupName(),
            attr.getLocationName(),
            attr.getTypeName(),
            attr.getSourceName(),
            attr.getType(),
            attr.getXUnits(),
            attr.getYUnits(),
            attr.getXType(),
            attr.getYType());
        setOriginalUnits(attr.getYUnits());
    }

    /**
     *
     */
    public String getOriginalUnits() {
        return _originalUnits;
    }

    /**
     *
     */
    private void setOriginalUnits(String units) {
        _originalUnits = units;
    }

    private String _originalUnits;
}
