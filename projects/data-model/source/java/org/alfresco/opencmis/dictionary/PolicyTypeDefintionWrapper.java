/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.opencmis.dictionary;

import java.util.ArrayList;
import java.util.Collection;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.opencmis.CMISUtils;
import org.alfresco.opencmis.dictionary.CMISAbstractDictionaryService.DictionaryRegistry;
import org.alfresco.opencmis.mapping.CMISMapping;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PolicyTypeDefinitionImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PolicyTypeDefintionWrapper extends AbstractTypeDefinitionWrapper
{
    private static final long serialVersionUID = 1L;
    // Logger
    protected static final Log logger = LogFactory.getLog(PolicyTypeDefintionWrapper.class);

    private PolicyTypeDefinitionImpl typeDef;
    private PolicyTypeDefinitionImpl typeDefInclProperties;

    public PolicyTypeDefintionWrapper(CMISMapping cmisMapping, PropertyAccessorMapping propertyAccessorMapping, 
            PropertyLuceneBuilderMapping luceneBuilderMapping, String typeId, DictionaryService dictionaryService, ClassDefinition cmisClassDef)
    {
        alfrescoName = cmisClassDef.getName();
        alfrescoClass = cmisMapping.getAlfrescoClass(alfrescoName);

        typeDef = new PolicyTypeDefinitionImpl();

        typeDef.setBaseTypeId(BaseTypeId.CMIS_POLICY);
        typeDef.setId(typeId);
        typeDef.setLocalName(alfrescoName.getLocalName());
        typeDef.setLocalNamespace(alfrescoName.getNamespaceURI());

        if (BaseTypeId.CMIS_POLICY.value().equals(typeId))
        {
            typeDef.setQueryName(ISO9075.encodeSQL(typeId));
            typeDef.setParentTypeId(null);
        } else
        {
            typeDef.setQueryName(ISO9075.encodeSQL(cmisMapping.buildPrefixEncodedString(alfrescoName)));
            QName parentQName = cmisMapping.getCmisType(cmisClassDef.getParentName());
            if (parentQName == null)
            {
                typeDef.setParentTypeId(cmisMapping.getCmisTypeId(CMISMapping.ASPECTS_QNAME));
            } else if (cmisMapping.isValidCmisPolicy(parentQName))
            {
                typeDef.setParentTypeId(cmisMapping.getCmisTypeId(BaseTypeId.CMIS_POLICY, parentQName));
            } else
            {
                throw new IllegalStateException("The CMIS type model should ignore aspects that inherit from excluded aspects");
            }
        }

        typeDef.setDisplayName((cmisClassDef.getTitle(dictionaryService) != null) ? cmisClassDef.getTitle(dictionaryService) : typeId);
        typeDef.setDescription(cmisClassDef.getDescription(dictionaryService) != null ? cmisClassDef.getDescription(dictionaryService) : typeDef
                .getDisplayName());

        typeDef.setIsCreatable(false);
        typeDef.setIsQueryable(true);
        typeDef.setIsFulltextIndexed(true);
        typeDef.setIsControllablePolicy(false);
        typeDef.setIsControllableAcl(false);
        typeDef.setIsIncludedInSupertypeQuery(cmisClassDef.getIncludedInSuperTypeQuery());
        typeDef.setIsFileable(false);

        typeDefInclProperties = CMISUtils.copy(typeDef);
        setTypeDefinition(typeDef, typeDefInclProperties);

        createOwningPropertyDefinitions(cmisMapping, propertyAccessorMapping, luceneBuilderMapping, dictionaryService, cmisClassDef);
        createActionEvaluators(propertyAccessorMapping, BaseTypeId.CMIS_POLICY);
    }

    public void connectParentAndSubTypes(CMISMapping cmisMapping, DictionaryRegistry registry,
            DictionaryService dictionaryService)
    {
        // find parent
        if (typeDef.getParentTypeId() != null)
        {
            parent = registry.typeDefsByTypeId.get(typeDef.getParentTypeId());
        } else
        {
            if (!isBaseType())
            {
                throw new AlfrescoRuntimeException("Type " + typeDef.getId() + " has no parent!");
            }

            parent = null;
        }

        // find children
        children = new ArrayList<TypeDefinitionWrapper>();
        Collection<QName> childrenNames = null;

        if (isBaseType())
        {
            // add the "Aspects" type to the CMIS Policy type
            childrenNames = new ArrayList<QName>();
            childrenNames.add(CMISMapping.ASPECTS_QNAME);
        } else if (getAlfrescoName().equals(CMISMapping.ASPECTS_QNAME))
        {
            // add all root aspects to the "Aspects" type
            childrenNames = new ArrayList<QName>();

            String aspectsTypeId = cmisMapping.getCmisTypeId(CMISMapping.ASPECTS_QNAME);
            for (AbstractTypeDefinitionWrapper tdw : registry.typeDefsByTypeId.values())
            {
                String parentId = tdw.getTypeDefinition(false).getParentTypeId();
                if ((parentId != null) && parentId.equals(aspectsTypeId))
                {
                    childrenNames.add(tdw.getAlfrescoName());
                }
            }
        } else
        {
            // add all non-root aspects to their parent
            childrenNames = dictionaryService.getSubAspects(cmisMapping.getAlfrescoClass(getAlfrescoName()), false);
        }

        for (QName childName : childrenNames)
        {
            if (cmisMapping.isValidCmisPolicy(childName))
            {
                TypeDefinitionWrapper child = registry.typeDefsByQName.get(childName);

                if (child == null)
                {
                    throw new AlfrescoRuntimeException("Failed to retrieve sub type for type id " + childName
                            + " for parent type " + getAlfrescoName() + "!");
                }
                children.add(child);
            }
            else
            {
                logger.info("Not a policy: " + childName);
            }
        }
    }

    public void resolveInheritance(CMISMapping cmisMapping,
            DictionaryRegistry registry, DictionaryService dictionaryService)
    {
        PropertyDefinition<?> propertyDefintion;

        if (parent != null)
        {
            for (PropertyDefinitionWrapper propDef : parent.getProperties())
            {
                if (propertiesById.containsKey(propDef.getPropertyId()))
                {
                    continue;
                }

                org.alfresco.service.cmr.dictionary.PropertyDefinition alfrescoPropDef = dictionaryService.getProperty(
                        propDef.getOwningType().getAlfrescoName(), propDef.getAlfrescoName());

                propertyDefintion = createPropertyDefinition(cmisMapping, propDef.getPropertyId(),
                        alfrescoPropDef.getName(), dictionaryService, alfrescoPropDef, true);

                if (propertyDefintion != null)
                {
                    registerProperty(new BasePropertyDefintionWrapper(propertyDefintion, alfrescoPropDef.getName(),
                            propDef.getOwningType(), propDef.getPropertyAccessor(), propDef.getPropertyLuceneBuilder()));
                }
            }
        }

        for (TypeDefinitionWrapper child : children)
        {
            if (child instanceof AbstractTypeDefinitionWrapper)
            {
                ((AbstractTypeDefinitionWrapper) child).resolveInheritance(cmisMapping, registry,
                        dictionaryService);
            }
        }
    }
}
