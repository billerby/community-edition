/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.repo.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import junit.framework.TestCase;

import org.alfresco.repo.cache.MemoryCache;
import org.alfresco.repo.dictionary.DictionaryDAOImpl.DictionaryRegistry;
import org.alfresco.repo.dictionary.NamespaceDAOImpl.NamespaceRegistry;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.dictionary.constraint.RegexConstraint;
import org.alfresco.repo.dictionary.constraint.RegisteredConstraint;
import org.alfresco.repo.dictionary.constraint.StringLengthConstraint;
import org.alfresco.repo.i18n.StaticMessageLookup;
import org.alfresco.repo.tenant.SingleTServiceImpl;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.surf.util.I18NUtil;


public class DictionaryDAOTest extends TestCase
{
    public static final String TEST_RESOURCE_MESSAGES = "alfresco/messages/dictionary-messages";

    private static final String TEST_URL = "http://www.alfresco.org/test/dictionarydaotest/1.0";
    private static final String TEST2_URL = "http://www.alfresco.org/test/dictionarydaotest2/1.0";
    private static final String TEST_MODEL = "org/alfresco/repo/dictionary/dictionarydaotest_model.xml";
    private static final String TEST_BUNDLE = "org/alfresco/repo/dictionary/dictionarydaotest_model";
    private static final String TEST_COMMON_NS_PARENT_MODEL = "org/alfresco/repo/dictionary/commonpropertynsparent_model.xml";
    private static final String TEST_COMMON_NS_CHILD_MODEL = "org/alfresco/repo/dictionary/commonpropertynschild_model.xml";
    private DictionaryService service;
    
    
    @Override
    public void setUp()
    {   
        // register resource bundles for messages
        I18NUtil.registerResourceBundle(TEST_RESOURCE_MESSAGES);
        
        // Instantiate Dictionary Service
        TenantService tenantService = new SingleTServiceImpl();   
        NamespaceDAOImpl namespaceDAO = new NamespaceDAOImpl();
        namespaceDAO.setTenantService(tenantService);
        initNamespaceCaches(namespaceDAO);
        
        DictionaryDAOImpl dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
        dictionaryDAO.setTenantService(tenantService);
        initDictionaryCaches(dictionaryDAO);

        // Populate with appropriate models
        DictionaryBootstrap bootstrap = new DictionaryBootstrap();
        List<String> bootstrapModels = new ArrayList<String>();
        bootstrapModels.add("alfresco/model/dictionaryModel.xml");
        bootstrapModels.add(TEST_MODEL);
        List<String> labels = new ArrayList<String>();
        labels.add(TEST_BUNDLE);
        bootstrap.setModels(bootstrapModels);
        bootstrap.setLabels(labels);
        bootstrap.setDictionaryDAO(dictionaryDAO);
        bootstrap.setTenantService(tenantService);
        bootstrap.bootstrap();
        
        DictionaryComponent component = new DictionaryComponent();
        component.setDictionaryDAO(dictionaryDAO);
        component.setMessageLookup(new StaticMessageLookup());
        service = component;
    }
    
    private void initDictionaryCaches(DictionaryDAOImpl dictionaryDAO)
    {
        dictionaryDAO.setDictionaryRegistryCache(new MemoryCache<String, DictionaryRegistry>());
    }
    
    private void initNamespaceCaches(NamespaceDAOImpl namespaceDAO)
    {
        namespaceDAO.setNamespaceRegistryCache(new MemoryCache<String, NamespaceRegistry>());
    }
    

    public void testBootstrap()
    {
        TenantService tenantService = new SingleTServiceImpl();   
        NamespaceDAOImpl namespaceDAO = new NamespaceDAOImpl();
        namespaceDAO.setTenantService(tenantService);
        initNamespaceCaches(namespaceDAO);
        
        DictionaryDAOImpl dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
        dictionaryDAO.setTenantService(tenantService);
        initDictionaryCaches(dictionaryDAO);
        
        DictionaryBootstrap bootstrap = new DictionaryBootstrap();
        List<String> bootstrapModels = new ArrayList<String>();
        
        bootstrapModels.add("alfresco/model/dictionaryModel.xml");
        
        bootstrap.setModels(bootstrapModels);
        bootstrap.setDictionaryDAO(dictionaryDAO);
        bootstrap.setTenantService(tenantService);
        bootstrap.bootstrap();
    }

    public void testUseImportedNamespaces()
    {
        TenantService tenantService = new SingleTServiceImpl();
        NamespaceDAOImpl namespaceDAO = new NamespaceDAOImpl();
        namespaceDAO.setTenantService(tenantService);
        initNamespaceCaches(namespaceDAO);
        
        DictionaryDAOImpl dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
        dictionaryDAO.setTenantService(tenantService);
        initDictionaryCaches(dictionaryDAO);
        
        DictionaryBootstrap bootstrap = new DictionaryBootstrap();
        List<String> bootstrapModels = new ArrayList<String>();
        
        bootstrapModels.add("alfresco/model/dictionaryModel.xml");
        bootstrapModels.add(TEST_COMMON_NS_PARENT_MODEL);
        bootstrapModels.add(TEST_COMMON_NS_CHILD_MODEL);
        
        bootstrap.setModels(bootstrapModels);
        bootstrap.setDictionaryDAO(dictionaryDAO);
        bootstrap.setTenantService(tenantService);
        bootstrap.bootstrap();
    }

    public void testLabels()
    {
        QName model = QName.createQName(TEST_URL, "dictionarydaotest");
        ModelDefinition modelDef = service.getModel(model);
        assertEquals("Model Description", modelDef.getDescription(service));
        
        QName type = QName.createQName(TEST_URL, "base");
        TypeDefinition typeDef = service.getType(type);
        assertEquals("Base Title", typeDef.getTitle(service));
        assertEquals("Base Description", typeDef.getDescription(service));
        
        QName prop = QName.createQName(TEST_URL, "prop1");
        PropertyDefinition propDef = service.getProperty(prop);
        assertEquals("Prop1 Title", propDef.getTitle(service));
        assertEquals("Prop1 Description", propDef.getDescription(service));
        
        QName assoc = QName.createQName(TEST_URL, "assoc1");
        AssociationDefinition assocDef = service.getAssociation(assoc);
        assertEquals("Assoc1 Title", assocDef.getTitle(service));
        assertEquals("Assoc1 Description", assocDef.getDescription(service));
        
        QName datatype = QName.createQName(TEST_URL, "datatype");
        DataTypeDefinition datatypeDef = service.getDataType(datatype);
        assertEquals("alfresco/model/dataTypeAnalyzers", datatypeDef.getAnalyserResourceBundleName());
        
        QName constraint = QName.createQName(TEST_URL, "list1");
        ConstraintDefinition constraintDef = service.getConstraint(constraint);
        assertEquals("List1 title", constraintDef.getTitle(service));
        assertEquals("List1 description", constraintDef.getDescription(service));
        
        
        // Localisation of List Of Values Constraint.
        // 1. LoV defined at the top of the model.
        ListOfValuesConstraint lovConstraint = (ListOfValuesConstraint)constraintDef.getConstraint();
        assertEquals("Wrong localised lov value.", "ABC display", lovConstraint.getDisplayLabel("ABC", service));
        assertEquals("Wrong localised lov value.", "DEF display", lovConstraint.getDisplayLabel("DEF", service));
        assertEquals("Wrong localised lov value.", "VALUE WITH SPACES display", lovConstraint.getDisplayLabel("VALUE WITH SPACES", service)); // Keys with spaces.
        assertNull(lovConstraint.getDisplayLabel("nosuchLOV", service));
        
        // 2. A named LoV defined within a specific property "non-Ref".
        QName constrainedPropName = QName.createQName(TEST_URL, "constrainedProp");
        PropertyDefinition constrainedPropDef = service.getProperty(constrainedPropName);
        List<ConstraintDefinition> constraints = constrainedPropDef.getConstraints();
        assertEquals("Wrong number of constraints.", 1, constraints.size());
        ConstraintDefinition inlineConstraintDef = constraints.get(0);
        lovConstraint = (ListOfValuesConstraint)inlineConstraintDef.getConstraint();
        assertEquals("Wrong localised lov value.", "ALPHA display", lovConstraint.getDisplayLabel("ALPHA", service));
        assertEquals("Wrong localised lov value.", "BETA display", lovConstraint.getDisplayLabel("BETA", service));
        assertEquals("Wrong localised lov value.", "GAMMA, DELTA display", lovConstraint.getDisplayLabel("GAMMA, DELTA", service)); // Keys with commas
        assertEquals("Wrong localised lov value.", "OMEGA", lovConstraint.getDisplayLabel("OMEGA", service));
        assertNull(lovConstraint.getDisplayLabel("nosuchLOV", service));
        
        // Localisation of unnamed LoV defined within a specific property are not supported.
    }
    
    public void testConstraints()
    {   
        QName model = QName.createQName(TEST_URL, "dictionarydaotest");
        Collection<ConstraintDefinition> modelConstraints = service.getConstraints(model);
        assertEquals(23, modelConstraints.size()); // 10 + 7 + 6
        
        QName conRegExp1QName = QName.createQName(TEST_URL, "regex1");
        boolean found1 = false;
        
        QName conStrLen1QName = QName.createQName(TEST_URL, "stringLength1");
        boolean found2 = false;
        
        for (ConstraintDefinition constraintDef : modelConstraints)
        {
            if (constraintDef.getName().equals(conRegExp1QName))
            {
                assertEquals("Regex1 title", constraintDef.getTitle(service));
                assertEquals("Regex1 description", constraintDef.getDescription(service));
                found1 = true;
            }
            
            if (constraintDef.getName().equals(conStrLen1QName))
            {
                assertNull(constraintDef.getTitle(service));
                assertNull(constraintDef.getDescription(service));
                found2 = true;
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        
        // get the constraints for a property without constraints
        QName propNoConstraintsQName = QName.createQName(TEST_URL, "fileprop");
        PropertyDefinition propNoConstraintsDef = service.getProperty(propNoConstraintsQName);
        assertNotNull("Property without constraints returned null list", propNoConstraintsDef.getConstraints());
        
        // get the constraints defined for the property
        QName prop1QName = QName.createQName(TEST_URL, "prop1");
        PropertyDefinition propDef = service.getProperty(prop1QName);
        List<ConstraintDefinition> constraints = propDef.getConstraints();
        assertNotNull("Null constraints list", constraints);
        assertEquals("Incorrect number of constraints", 3, constraints.size());
        assertTrue("Constraint instance incorrect", constraints.get(0).getConstraint() instanceof RegexConstraint);
        assertTrue("Constraint instance incorrect", constraints.get(1).getConstraint() instanceof StringLengthConstraint);
        assertTrue("Constraint instance incorrect", constraints.get(2).getConstraint() instanceof RegisteredConstraint);
        
        // check the individual constraints
        ConstraintDefinition constraintDef = constraints.get(0);
        assertTrue("Constraint anonymous name incorrect", constraintDef.getName().getLocalName().equals("dictionarydaotest_base_prop1_anon_0"));
        
        // inherit title / description for reference constraint
        assertTrue("Constraint title incorrect", constraintDef.getTitle(service).equals("Regex1 title"));
        assertTrue("Constraint description incorrect", constraintDef.getDescription(service).equals("Regex1 description"));
        
        constraintDef = constraints.get(1);
        assertTrue("Constraint anonymous name incorrect", constraintDef.getName().getLocalName().equals("dictionarydaotest_base_prop1_anon_1"));
        
        assertTrue("Constraint title incorrect", constraintDef.getTitle(service).equals("Prop1 Strlen1 title"));
        assertTrue("Constraint description incorrect", constraintDef.getDescription(service).equals("Prop1 Strlen1 description"));
        
        // check that the constraint implementation is valid (it used a reference)
        Constraint constraint = constraintDef.getConstraint();
        assertNotNull("Reference constraint has no implementation", constraint);
    }
    
    public void testConstraintsOverrideInheritance()
    {
        QName baseQName = QName.createQName(TEST_URL, "base");
        QName fileQName = QName.createQName(TEST_URL, "file");
        QName folderQName = QName.createQName(TEST_URL, "folder");
        QName prop1QName = QName.createQName(TEST_URL, "prop1");

        // get the base property
        PropertyDefinition prop1Def = service.getProperty(baseQName, prop1QName);
        assertNotNull(prop1Def);
        List<ConstraintDefinition> prop1Constraints = prop1Def.getConstraints();
        assertEquals("Incorrect number of constraints", 3, prop1Constraints.size());
        assertTrue("Constraint instance incorrect", prop1Constraints.get(0).getConstraint() instanceof RegexConstraint);
        assertTrue("Constraint instance incorrect", prop1Constraints.get(1).getConstraint() instanceof StringLengthConstraint);
        assertTrue("Constraint instance incorrect", prop1Constraints.get(2).getConstraint() instanceof RegisteredConstraint);

        // check the inherited property on folder (must be same as above)
        prop1Def = service.getProperty(folderQName, prop1QName);
        assertNotNull(prop1Def);
        prop1Constraints = prop1Def.getConstraints();
        assertEquals("Incorrect number of constraints", 3, prop1Constraints.size());
        assertTrue("Constraint instance incorrect", prop1Constraints.get(0).getConstraint() instanceof RegexConstraint);
        assertTrue("Constraint instance incorrect", prop1Constraints.get(1).getConstraint() instanceof StringLengthConstraint);
        assertTrue("Constraint instance incorrect", prop1Constraints.get(2).getConstraint() instanceof RegisteredConstraint);

        // check the overridden property on file (must be reverse of above)
        prop1Def = service.getProperty(fileQName, prop1QName);
        assertNotNull(prop1Def);
        prop1Constraints = prop1Def.getConstraints();
        assertEquals("Incorrect number of constraints", 3, prop1Constraints.size());
        assertTrue("Constraint instance incorrect", prop1Constraints.get(0).getConstraint() instanceof StringLengthConstraint);
        assertTrue("Constraint instance incorrect", prop1Constraints.get(1).getConstraint() instanceof RegexConstraint);
        assertTrue("Constraint instance incorrect", prop1Constraints.get(2).getConstraint() instanceof RegisteredConstraint);
    }
    
    public void testConstraintsOverrideInheritanceOnAspects()
    {
        QName aspectBaseQName = QName.createQName(TEST_URL, "aspect-base");
        QName aspectOneQName = QName.createQName(TEST_URL, "aspect-one");
        QName aspectTwoQName = QName.createQName(TEST_URL, "aspect-two");
        QName aspectThreeQName = QName.createQName(TEST2_URL, "aspect-three");
        QName propQName = QName.createQName(TEST_URL, "aspect-base-p1");

        // get the base property
        PropertyDefinition propDef = service.getProperty(aspectBaseQName, propQName);
        assertNotNull(propDef);
        List<ConstraintDefinition> propConstraints = propDef.getConstraints();
        assertEquals("Incorrect number of constraints", 1, propConstraints.size());
        assertTrue("Constraint instance incorrect", propConstraints.get(0).getConstraint() instanceof ListOfValuesConstraint);
        ListOfValuesConstraint constraint = (ListOfValuesConstraint) propConstraints.get(0).getConstraint();
        List<String> allowedValues = constraint.getAllowedValues();
        assertEquals("Expected 3 allowed values", 3, allowedValues.size());
        assertEquals("ABC", allowedValues.get(0));
        assertEquals("DEF", allowedValues.get(1));
        assertEquals("VALUE WITH SPACES", allowedValues.get(2));

        // check the inherited property on first derived aspect
        propDef = service.getProperty(aspectOneQName, propQName);
        assertNotNull(propDef);
        propConstraints = propDef.getConstraints();
        assertEquals("Incorrect number of constraints", 1, propConstraints.size());
        assertTrue("Constraint instance incorrect", propConstraints.get(0).getConstraint() instanceof ListOfValuesConstraint);
        constraint = (ListOfValuesConstraint) propConstraints.get(0).getConstraint();
        allowedValues = constraint.getAllowedValues();
        assertEquals("Expected 1 allowed values", 1, allowedValues.size());
        assertEquals("HIJ", allowedValues.get(0));
        
        // check the inherited property on second derived aspect
        propDef = service.getProperty(aspectTwoQName, propQName);
        assertNotNull(propDef);
        propConstraints = propDef.getConstraints();
        assertEquals("Incorrect number of constraints", 2, propConstraints.size());
        assertTrue("Constraint instance incorrect", propConstraints.get(0).getConstraint() instanceof ListOfValuesConstraint);
        assertTrue("Constraint instance incorrect", propConstraints.get(1).getConstraint() instanceof ListOfValuesConstraint);
        constraint = (ListOfValuesConstraint) propConstraints.get(0).getConstraint();
        allowedValues = constraint.getAllowedValues();
        assertEquals("Wrong number of allowed values", 3, allowedValues.size());
        assertEquals("ABC", allowedValues.get(0));
        assertEquals("DEF", allowedValues.get(1));
        assertEquals("VALUE WITH SPACES", allowedValues.get(2));
        constraint = (ListOfValuesConstraint) propConstraints.get(1).getConstraint();
        allowedValues = constraint.getAllowedValues();
        assertEquals("Wrong number of allowed values", 1, allowedValues.size());
        assertEquals("HIJ", allowedValues.get(0));
        
        // check the cross-namespace inheritance
        propDef = service.getProperty(aspectThreeQName, propQName);
        assertNotNull(propDef);
        propConstraints = propDef.getConstraints();
        assertEquals("Incorrect number of constraints", 1, propConstraints.size());
        assertTrue("Constraint instance incorrect", propConstraints.get(0).getConstraint() instanceof ListOfValuesConstraint);
        constraint = (ListOfValuesConstraint) propConstraints.get(0).getConstraint();
        allowedValues = constraint.getAllowedValues();
        assertEquals("Expected 1 allowed values", 1, allowedValues.size());
        assertEquals("XYZ", allowedValues.get(0));
    }

    public void testArchive()
    {
        QName testFileQName = QName.createQName(TEST_URL, "file");
        ClassDefinition fileClassDef = service.getClass(testFileQName);
        assertTrue("File type should have the archive flag", fileClassDef.getArchive());

        QName testFileDerivedQName = QName.createQName(TEST_URL, "file-derived");
        ClassDefinition fileDerivedClassDef = service.getClass(testFileDerivedQName);
        assertTrue("Direct derived File type should have the archive flag", fileDerivedClassDef.getArchive());

        QName testFileDerivedNoArchiveQName = QName.createQName(TEST_URL, "file-derived-no-archive");
        ClassDefinition fileDerivedNoArchiveClassDef = service.getClass(testFileDerivedNoArchiveQName);
        assertFalse("Derived File with archive override type should NOT have the archive flag",
                fileDerivedNoArchiveClassDef.getArchive());

        QName testFolderQName = QName.createQName(TEST_URL, "folder");
        ClassDefinition folderClassDef = service.getClass(testFolderQName);
        assertNull("Folder type should not have the archive flag", folderClassDef.getArchive());
    }
    
    public void testMandatoryEnforced()
    {
        // get the properties for the test type
        QName testEnforcedQName = QName.createQName(TEST_URL, "enforced");
        ClassDefinition testEnforcedClassDef = service.getClass(testEnforcedQName);
        Map<QName, PropertyDefinition> testEnforcedPropertyDefs = testEnforcedClassDef.getProperties();
        
        PropertyDefinition propertyDef = null;

        QName testMandatoryEnforcedQName = QName.createQName(TEST_URL, "mandatory-enforced");
        propertyDef = testEnforcedPropertyDefs.get(testMandatoryEnforcedQName);
        assertNotNull("Property not found: " + testMandatoryEnforcedQName,
                propertyDef);
        assertTrue("Expected property to be mandatory: " + testMandatoryEnforcedQName,
                propertyDef.isMandatory());
        assertTrue("Expected property to be mandatory-enforced: " + testMandatoryEnforcedQName,
                propertyDef.isMandatoryEnforced());

        QName testMandatoryNotEnforcedQName = QName.createQName(TEST_URL, "mandatory-not-enforced");
        propertyDef = testEnforcedPropertyDefs.get(testMandatoryNotEnforcedQName);
        assertNotNull("Property not found: " + testMandatoryNotEnforcedQName,
                propertyDef);
        assertTrue("Expected property to be mandatory: " + testMandatoryNotEnforcedQName,
                propertyDef.isMandatory());
        assertFalse("Expected property to be mandatory-not-enforced: " + testMandatoryNotEnforcedQName,
                propertyDef.isMandatoryEnforced());

        QName testMandatoryDefaultEnforcedQName = QName.createQName(TEST_URL, "mandatory-default-enforced");
        propertyDef = testEnforcedPropertyDefs.get(testMandatoryDefaultEnforcedQName);
        assertNotNull("Property not found: " + testMandatoryDefaultEnforcedQName,
                propertyDef);
        assertTrue("Expected property to be mandatory: " + testMandatoryDefaultEnforcedQName,
                propertyDef.isMandatory());
        assertFalse("Expected property to be mandatory-not-enforced: " + testMandatoryDefaultEnforcedQName,
                propertyDef.isMandatoryEnforced());
    }
    
    public void testSubClassOf()
    {
        QName invalid = QName.createQName(TEST_URL, "invalid");
        QName base = QName.createQName(TEST_URL, "base");
        QName file = QName.createQName(TEST_URL, "file");
        QName folder = QName.createQName(TEST_URL, "folder");
        QName referenceable = QName.createQName(TEST_URL, "referenceable");

        // Test invalid args
        boolean testI1 = service.isSubClass(invalid, referenceable);
        
        assertFalse(testI1);
        
        boolean testI2 = service.isSubClass(referenceable, invalid);
        assertFalse(testI2);
        
        boolean testI3 = service.isSubClass(invalid, invalid);
        assertFalse(testI3);

        // Test various flavours of subclassof
        boolean test1 = service.isSubClass(file, referenceable);  // type vs aspect
        assertFalse(test1);
        boolean test2 = service.isSubClass(file, folder);   // seperate hierarchies
        assertFalse(test2);
        boolean test3 = service.isSubClass(file, file);   // self
        assertTrue(test3);
        boolean test4 = service.isSubClass(folder, base);  // subclass
        assertTrue(test4);
        boolean test5 = service.isSubClass(base, folder);  // reversed test
        assertFalse(test5);
    }
    

    public void testPropertyOverride()
    {
        TypeDefinition type1 = service.getType(QName.createQName(TEST_URL, "overridetype1"));
        Map<QName, PropertyDefinition> props1 = type1.getProperties();
        PropertyDefinition prop1 = props1.get(QName.createQName(TEST_URL, "propoverride"));
        String def1 = prop1.getDefaultValue();
        assertEquals("one", def1);
        
        TypeDefinition type2 = service.getType(QName.createQName(TEST_URL, "overridetype2"));
        Map<QName, PropertyDefinition> props2 = type2.getProperties();
        PropertyDefinition prop2 = props2.get(QName.createQName(TEST_URL, "propoverride"));
        String def2 = prop2.getDefaultValue();
        assertEquals("two", def2);

        TypeDefinition type3 = service.getType(QName.createQName(TEST_URL, "overridetype3"));
        Map<QName, PropertyDefinition> props3 = type3.getProperties();
        PropertyDefinition prop3 = props3.get(QName.createQName(TEST_URL, "propoverride"));
        String def3 = prop3.getDefaultValue();
        assertEquals("three", def3);
    }

    public void testChildAssocPropagate()
    {
        // Check the default value
        AssociationDefinition assocDef = service.getAssociation(QName.createQName(TEST_URL, "childassoc1"));
        assertNotNull("No such child association found", assocDef);
        assertTrue("Expected a child association", assocDef instanceof ChildAssociationDefinition);
        ChildAssociationDefinition childAssocDef = (ChildAssociationDefinition) assocDef;
        assertFalse("Expected 'false' for default timestamp propagation", childAssocDef.getPropagateTimestamps());

        // Check the explicit value
        assocDef = service.getAssociation(QName.createQName(TEST_URL, "childassocPropagate"));
        assertNotNull("No such child association found", assocDef);
        assertTrue("Expected a child association", assocDef instanceof ChildAssociationDefinition);
        childAssocDef = (ChildAssociationDefinition) assocDef;
        assertTrue("Expected 'true' for timestamp propagation", childAssocDef.getPropagateTimestamps());
    }
    
    public void testDataTypeAnlyserResolution()
    {
        // Stuff to configure/
        
        TenantService tenantService = new SingleTServiceImpl();   
        NamespaceDAOImpl namespaceDAO = new NamespaceDAOImpl();
        namespaceDAO.setTenantService(tenantService);
        initNamespaceCaches(namespaceDAO);
        
        DictionaryDAOImpl dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
        dictionaryDAO.setTenantService(tenantService);
        initDictionaryCaches(dictionaryDAO);
      
        ModelDefinition modelDefinition;
        DataTypeDefinition dataTypeDefinition;
        
        //
        
        dictionaryDAO.putModel(createModel(dictionaryDAO, false, false, false));
        QName modeQName = QName.createQName("test:analyzerModel", namespaceDAO);
        QName dataTypeQName =  QName.createQName("test:analyzerDataType", namespaceDAO);
        
        modelDefinition = dictionaryDAO.getModel(modeQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), null);
        dataTypeDefinition = dictionaryDAO.getDataType(dataTypeQName);
        assertEquals(dataTypeDefinition.getAnalyserResourceBundleName(), null);
        assertEquals(dataTypeDefinition.getDefaultAnalyserClassName(), null);
        
        assertNull(dataTypeDefinition.resolveAnalyserClassName());
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("defaultBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modeQName);
        
        //
        
        dictionaryDAO.putModel(createModel(dictionaryDAO, false, false, true));
        
        modelDefinition = dictionaryDAO.getModel(modeQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), null);
        dataTypeDefinition = dictionaryDAO.getDataType(dataTypeQName);
        assertEquals(dataTypeDefinition.getAnalyserResourceBundleName(), null);
        assertEquals(dataTypeDefinition.getDefaultAnalyserClassName(), "java.lang.String");
        
        assertEquals(dataTypeDefinition.resolveAnalyserClassName(), "java.lang.String");
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("defaultBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modeQName);
        
        //
         
        dictionaryDAO.putModel(createModel(dictionaryDAO, false, true, false));
        
        modelDefinition = dictionaryDAO.getModel(modeQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), null);
        dataTypeDefinition = dictionaryDAO.getDataType(dataTypeQName);
        assertEquals(dataTypeDefinition.getAnalyserResourceBundleName(), "dataTypeResourceBundle");
        assertEquals(dataTypeDefinition.getDefaultAnalyserClassName(), null);
        
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("dataTypeResourceBundle"));
        }
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("defaultBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modeQName);
        
        //
        
        dictionaryDAO.putModel(createModel(dictionaryDAO, false, true, true));
        
        modelDefinition = dictionaryDAO.getModel(modeQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), null);
        dataTypeDefinition = dictionaryDAO.getDataType(dataTypeQName);
        assertEquals(dataTypeDefinition.getAnalyserResourceBundleName(), "dataTypeResourceBundle");
        assertEquals(dataTypeDefinition.getDefaultAnalyserClassName(), "java.lang.String");
        
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("dataTypeResourceBundle"));
        }
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("defaultBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modeQName);
       
        
//
        
        dictionaryDAO.putModel(createModel(dictionaryDAO, true, false, false));
     
        
        modelDefinition = dictionaryDAO.getModel(modeQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), "dataTypeModelResourceBundle");
        dataTypeDefinition = dictionaryDAO.getDataType(dataTypeQName);
        assertEquals(dataTypeDefinition.getAnalyserResourceBundleName(), null);
        assertEquals(dataTypeDefinition.getDefaultAnalyserClassName(), null);
        
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("dataTypeModelResourceBundle"));
        }
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("defaultBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modeQName);
        
        //
        
        dictionaryDAO.putModel(createModel(dictionaryDAO, true, false, true));
        
        modelDefinition = dictionaryDAO.getModel(modeQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), "dataTypeModelResourceBundle");
        dataTypeDefinition = dictionaryDAO.getDataType(dataTypeQName);
        assertEquals(dataTypeDefinition.getAnalyserResourceBundleName(), null);
        assertEquals(dataTypeDefinition.getDefaultAnalyserClassName(), "java.lang.String");
        
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("dataTypeModelResourceBundle"));
        }
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("defaultBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modeQName);
        
        //
         
        dictionaryDAO.putModel(createModel(dictionaryDAO, true, true, false));
        
        modelDefinition = dictionaryDAO.getModel(modeQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), "dataTypeModelResourceBundle");
        dataTypeDefinition = dictionaryDAO.getDataType(dataTypeQName);
        assertEquals(dataTypeDefinition.getAnalyserResourceBundleName(), "dataTypeResourceBundle");
        assertEquals(dataTypeDefinition.getDefaultAnalyserClassName(), null);
        
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("dataTypeResourceBundle"));
        }
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("defaultBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modeQName);
        
        //
        
        dictionaryDAO.putModel(createModel(dictionaryDAO, true, true, true));
        
        modelDefinition = dictionaryDAO.getModel(modeQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), "dataTypeModelResourceBundle");
        dataTypeDefinition = dictionaryDAO.getDataType(dataTypeQName);
        assertEquals(dataTypeDefinition.getAnalyserResourceBundleName(), "dataTypeResourceBundle");
        assertEquals(dataTypeDefinition.getDefaultAnalyserClassName(), "java.lang.String");
        
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("dataTypeResourceBundle"));
        }
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            dataTypeDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("defaultBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modeQName);
        
    }
    
    private M2Model createModel(DictionaryDAO dictionaryDAO, boolean withModelBundle, boolean withDataTypeBundle, boolean withTypeAnalyserClss)
    {
        String testNamespace = "http://www.alfresco.org/test/analyserResolution";
        M2Model model = M2Model.createModel("test:analyzerModel");
        model.createNamespace(testNamespace, "test");
        if(withModelBundle)
        {
            model.setAnalyserResourceBundleName("dataTypeModelResourceBundle");
        }
        
        M2DataType dataTypeWithAnalyserBundleName = model.createPropertyType("test:analyzerDataType");
        dataTypeWithAnalyserBundleName.setJavaClassName("java.lang.String");
        if(withTypeAnalyserClss)
        {
            dataTypeWithAnalyserBundleName.setDefaultAnalyserClassName("java.lang.String");
        }
        if(withDataTypeBundle)
        {
            dataTypeWithAnalyserBundleName.setAnalyserResourceBundleName("dataTypeResourceBundle");
        }
        return model;
    }
    
    public void testTypeAnalyserResolution()
    {
        // Stuff to configure/
        
        TenantService tenantService = new SingleTServiceImpl();   
        NamespaceDAOImpl namespaceDAO = new NamespaceDAOImpl();
        namespaceDAO.setTenantService(tenantService);
        initNamespaceCaches(namespaceDAO);
        
        DictionaryDAOImpl dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
        dictionaryDAO.setTenantService(tenantService);
        initDictionaryCaches(dictionaryDAO);
        
        // build data model - typical settings
        dictionaryDAO.putModel(createModel(dictionaryDAO, true, false, true));
        
        // check simple stack - all defined keep removing the end
      
        ModelDefinition modelDefinition;
        ClassDefinition superDefinition;
        ClassDefinition classDefinition;
        PropertyDefinition propertyDefinition;
        
        dictionaryDAO.putModel(createTypeModel(dictionaryDAO, true, true, true, true));
        
        QName modelQName = QName.createQName("test2:analyzerClassModel", namespaceDAO);
        QName superQName = QName.createQName("test2:analyzerSuperType", namespaceDAO);
        QName typeQName = QName.createQName("test2:analyzerType", namespaceDAO);
        QName propertyQName = QName.createQName("test2:analyzerProperty", namespaceDAO);
        
        modelDefinition = dictionaryDAO.getModel(modelQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), "typeModelResourceBundle");
        superDefinition = dictionaryDAO.getType(superQName);
        assertEquals(superDefinition.getAnalyserResourceBundleName(), "superTypeResourceBundle");
        classDefinition = dictionaryDAO.getType(typeQName);
        assertEquals(classDefinition.getAnalyserResourceBundleName(), "typeResourceBundle");
        propertyDefinition = dictionaryDAO.getProperty(propertyQName);
        assertEquals(propertyDefinition.getAnalyserResourceBundleName(), "propertyResourceBundle");
        
        try
        {
            propertyDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("propertyResourceBundle"));
        }
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            propertyDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("propertyResourceBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modelQName);
        
        //
        
        dictionaryDAO.putModel(createTypeModel(dictionaryDAO, true, true, true, false));
     
        modelDefinition = dictionaryDAO.getModel(modelQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), "typeModelResourceBundle");
        superDefinition = dictionaryDAO.getType(superQName);
        assertEquals(superDefinition.getAnalyserResourceBundleName(), "superTypeResourceBundle");
        classDefinition = dictionaryDAO.getType(typeQName);
        assertEquals(classDefinition.getAnalyserResourceBundleName(), "typeResourceBundle");
        propertyDefinition = dictionaryDAO.getProperty(propertyQName);
        assertEquals(propertyDefinition.getAnalyserResourceBundleName(), null);
        
        try
        {
            propertyDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("typeResourceBundle"));
        }
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            propertyDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("typeResourceBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modelQName);
        
//
        
        dictionaryDAO.putModel(createTypeModel(dictionaryDAO, true, true, false, false));
     
        modelDefinition = dictionaryDAO.getModel(modelQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), "typeModelResourceBundle");
        superDefinition = dictionaryDAO.getType(superQName);
        assertEquals(superDefinition.getAnalyserResourceBundleName(), "superTypeResourceBundle");
        classDefinition = dictionaryDAO.getType(typeQName);
        assertEquals(classDefinition.getAnalyserResourceBundleName(), null);
        propertyDefinition = dictionaryDAO.getProperty(propertyQName);
        assertEquals(propertyDefinition.getAnalyserResourceBundleName(), null);
        
        try
        {
            propertyDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("superTypeResourceBundle"));
        }
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            propertyDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("superTypeResourceBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modelQName);
        
//
        
        dictionaryDAO.putModel(createTypeModel(dictionaryDAO, true, false, false, false));
     
        modelDefinition = dictionaryDAO.getModel(modelQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), "typeModelResourceBundle");
        superDefinition = dictionaryDAO.getType(superQName);
        assertEquals(superDefinition.getAnalyserResourceBundleName(), null);
        classDefinition = dictionaryDAO.getType(typeQName);
        assertEquals(classDefinition.getAnalyserResourceBundleName(), null);
        propertyDefinition = dictionaryDAO.getProperty(propertyQName);
        assertEquals(propertyDefinition.getAnalyserResourceBundleName(), null);
        
        try
        {
            propertyDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("typeModelResourceBundle"));
        }
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            propertyDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("typeModelResourceBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modelQName);
        
//
        
        dictionaryDAO.putModel(createTypeModel(dictionaryDAO, false, false, false, false));
     
        modelDefinition = dictionaryDAO.getModel(modelQName);
        assertEquals(modelDefinition.getAnalyserResourceBundleName(), null);
        superDefinition = dictionaryDAO.getType(superQName);
        assertEquals(superDefinition.getAnalyserResourceBundleName(), null);
        classDefinition = dictionaryDAO.getType(typeQName);
        assertEquals(classDefinition.getAnalyserResourceBundleName(), null);
        propertyDefinition = dictionaryDAO.getProperty(propertyQName);
        assertEquals(propertyDefinition.getAnalyserResourceBundleName(), null);
        
        try
        {
            propertyDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("dataTypeModelResourceBundle"));
        }
        dictionaryDAO.setDefaultAnalyserResourceBundleName("defaultBundle");
        try
        {
            propertyDefinition.resolveAnalyserClassName();
            fail();
        }
        catch(MissingResourceException mre)
        {
            assertTrue(mre.getMessage().contains("defaultBundle"));
        }
        
        dictionaryDAO.setDefaultAnalyserResourceBundleName(null);
        dictionaryDAO.removeModel(modelQName);
        
        
    }

    /**
     * @param dictionaryDAO
     * @param b
     * @param c
     * @param d
     * @param e
     * @param f
     * @return
     */
    private M2Model createTypeModel(DictionaryDAOImpl dictionaryDAO, boolean withModelBundle, boolean withInheritedTypeBundle, boolean withTypeBundle, boolean withPropertyBundle)
    {
        String testNamespace = "http://www.alfresco.org/test/analyserResolutionType";
        M2Model model = M2Model.createModel("test2:analyzerClassModel");
        model.createNamespace(testNamespace, "test2");
        model.createImport("http://www.alfresco.org/test/analyserResolution", "test");
        if(withModelBundle)
        {
            model.setAnalyserResourceBundleName("typeModelResourceBundle");
        }
        
        M2Type superTypeWithAnalyserBundleName = model.createType("test2:analyzerSuperType");
        if(withInheritedTypeBundle)
        {
            superTypeWithAnalyserBundleName.setAnalyserResourceBundleName("superTypeResourceBundle");
        }
        
        M2Type typeWithAnalyserBundleName = model.createType("test2:analyzerType");
        typeWithAnalyserBundleName.setParentName("test2:analyzerSuperType");
        if(withTypeBundle)
        {
            typeWithAnalyserBundleName.setAnalyserResourceBundleName("typeResourceBundle");
        }
        
        M2Property propertyWithAnalyserBundelName = typeWithAnalyserBundleName.createProperty("test2:analyzerProperty");
        propertyWithAnalyserBundelName.setType("test:analyzerDataType");
        if(withPropertyBundle)
        {
            propertyWithAnalyserBundelName.setAnalyserResourceBundleName("propertyResourceBundle");
        }
        return model;
    }

}
