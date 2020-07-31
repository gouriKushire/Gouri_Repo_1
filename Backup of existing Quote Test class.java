/**************************************************************************************************************************************
* Test Class: SBQQQuoteTriggerTest
* Created by Jade Global
---------------------------------------------------------------------------------------------------------------------------------------
* Purpose/Methods:
* - Test class for Apex Class - SBQQQuoteTrigger
---------------------------------------------------------------------------------------------------------------------------------------
* History:
* - VERSION     DEVELOPER NAME          DATE                DETAIL FEATURES
    1.0         Jade Global             July 2017           INITIAL DEVELOPMENT
**************************************************************************************************************************************/   
@isTest
public class SBQQQuoteTriggerTest {
    public static Profile objProfile = new Profile();
    public static UserRole objUserRole = new UserRole();
    public static User objUser = new User();
    
    static testMethod void createData(){
        objProfile = [SELECT Id FROM Profile WHERE Name = 'System Administrator' Limit 1]; 

        objUserRole = [SELECT Id FROM UserRole WHERE Name LIKE '%Growth%' Limit 1];

        
        objUser = testDataFactory.createAdminUser(objProfile.id, objUserRole.id);
        insert objUser;    
        system.runAs(objUser){
            activateQuoteTrigger();
            
            List<Account> accountList = new List<Account>();
            List<Contact> contactsList = new List<Contact>();
            List<Opportunity> opportunityList = new List<Opportunity>();
            List<SBQQ__Quote__c> quotesList = new List<SBQQ__Quote__c>();
            List<Product2> productsToInsertList = new List<Product2>();
            List<PriceBook2> priceBooksToBeInsertedList = new List<PriceBook2>();
            List<PriceBookEntry> priceBookEntryList = new List<PriceBookEntry>();
            List<SBQQ__QuoteLine__c> quoteLinesToInsert = new List<SBQQ__QuoteLine__c>();
            List<SBQQ__Quote__c> quotesWithContractList = new List<SBQQ__Quote__c>();
            List<Contract> contractsList = new List<Contract>();
            
            
            accountList = TestCPQUtilityClass.createAccounts(2);
            accountList.get(0).Agency_Account__c = true;
            insert accountList ;

            contactsList = TestCPQUtilityClass.createContacts(accountList ,2);
            contactsList.get(0).FirstName  ='Test';
            contactsList.get(0).LastName  ='Contact';
            contactsList.get(0).Email  ='t@test.com';
            contactsList.get(1).FirstName  ='Test1';
            contactsList.get(1).LastName  ='Contact1';
            contactsList.get(1).Email  ='ttt@test.com';
            
            Contact c3 = new Contact(firstname='Test123', lastname='Test123', AccountId=accountList[0].Id); 
            contactsList.add(c3);
            
            insert contactsList; 

            opportunityList = TestCPQUtilityClass.createOpportunities(accountList,1);
            insert opportunityList;
            
            productsToInsertList = TestCPQUtilityClass.createCustomProducts(5);
            insert productsToInsertList;
            
            priceBooksToBeInsertedList = TestCPQUtilityClass.createCustomPriceBooks(2);
            insert priceBooksToBeInsertedList;
            
            TestCPQUtilityClass.createStdPricebookEntries(productsToInsertList,Test.getStandardPricebookId());
            
            priceBookEntryList = TestCPQUtilityClass.createPriceBookEntries(productsToInsertList,priceBooksToBeInsertedList);
            insert priceBookEntryList;
            
            List<Dynamic_Field_Sync__c> dyncSyncCustomsettings = testDataFactory.createDynamicFieldSyncCustomSettings();
            insert dyncSyncCustomsettings;
            
            contractsList = TestCPQUtilityClass.createContracts(accountList ,1);
            insert contractsList ;
            
           // SkipAutomationSetting__c customSettingObj = new SkipAutomationSetting__c(Opportunity_Automation__c = true);
            //insert customSettingObj;
          
            quotesList = TestCPQUtilityClass.createQuotes(opportunityList,2);
            
            quotesList.get(0).Legal_Special_Terms__c ='Assignment to Competitor';
            quotesList.get(0).Customer_PO__c = 'Testing';
            quotesList.get(0).SBQQ__Primary__c= true;
            quotesList.get(0).Deal_Category__c = 'New';
            quotesList.get(0).Deal_Type__c= 'New Business';
            quotesList.get(1).Customer_PO__c = 'Testing';

            insert quotesList;
            Test.startTest();
            quotesList.get(1).SBQQ__Type__c='Amendment';
            quotesList.get(1).SBQQ__MasterContract__c = contractsList.get(0).Id;            
            quotesList.get(0).Bill_To_Street__c ='Test';
            quotesList.get(0).Customer_PO__c = 'Test';
            quotesList.get(1).Customer_PO__c = 'Test';
            quotesList.get(0).Billing_Contact_First_Name__c = 'Test';
            quotesList.get(0).Billing_Contact_Last_Name__c ='Contact';
            quotesList.get(1).Billing_Contact_First_Name__c = 'Test Not Match';
            quotesList.get(1).Billing_Contact_Last_Name__c ='Contact Not Match';
            quotesList.get(0).Billing_Email__c = 't@test.com'; 
            quotesList.get(1).Billing_Email__c = 'tt@test.com'; 
            quotesList.get(1).Bill_To_Contact__c = contactsList.get(1).id;
            quotesList.get(1).Sold_To_Contact__c = contactsList.get(1).id;
            quotesList.get(1).Deal_Category__c = 'New';
            quotesList.get(1).Deal_Type__c= 'New Business';
            //quotesList.get(0).SBQQ__ListAmount__c = 1000;
            //quotesList.get(1).SBQQ__LineItemCount__c = 1;
            quotesList.get(0).SBQQ__Primary__c = true;
            quotesList.get(1).Agency_Account__c = accountList.get(0).id;           
            SBQQQuoteTriggerHelper.hasRun = false;
            
            update quotesList;
            SBQQ__Quote__c sq2 = [select id,Bill_To_Contact__c ,SBQQ__MasterContract__c ,SBQQ__Type__c,Customer_PO__c,Billing_Contact_First_Name__c,Billing_Contact_Last_Name__c,Billing_Email__c    from SBQQ__Quote__c where id =: quotesList.get(2).id];
             //system.assertequals(sq2.SBQQ__Type__c,'Amendment');
             //system.assertnotequals(sq2.SBQQ__MasterContract__c ,null);
             
            SBQQ__Quote__c sq1 = [select id,Bill_To_Contact__c ,Customer_PO__c,Billing_Contact_First_Name__c,Billing_Contact_Last_Name__c,Billing_Email__c    from SBQQ__Quote__c where id =: quotesList.get(0).id];
             //system.assertequals(sq1.Bill_To_Contact__c ,contactsList.get(0).id);
             system.assertequals(sq1.Billing_Contact_Last_Name__c  ,'Contact');
             system.assertequals(sq1.Billing_Email__c  ,'t@test.com');
             
            SBQQ__Quote__c sq3 = [select id,Bill_To_Contact__c ,Customer_PO__c,Billing_Contact_First_Name__c,Billing_Contact_Last_Name__c,Billing_Email__c    from SBQQ__Quote__c where id =: quotesList.get(1).id];
             
             
            quoteLinesToInsert = TestCPQUtilityClass.createQuoteLines(quotesList,2,productsToInsertList );
            insert quoteLinesToInsert;
            
            quotesList.get(0).Bill_To_Contact__c = contactsList.get(0).id;
            quotesList.get(0).Sold_To_Contact__c = contactsList.get(0).id;
            quotesList.get(1).SBQQ__Type__c='Amendment';
            quotesList.get(1).SBQQ__MasterContract__c = contractsList.get(0).Id;
            quotesList.get(1).Agency_Account__c = null; 
            quotesList.get(1).SBQQ__Type__c = 'Renewal';   
            quotesList.get(1).Deal_Category__c = 'New';
            quotesList.get(1).Deal_Type__c= 'New Business'; 
            quotesList.get(0).Billing_Contact_First_Name__c = 'Test First Name'; 
            quotesList.get(1).Billing_Contact_First_Name__c = 'Test First Name';  
            
            update quotesList;  
            try{        
            delete quotesList;
            }
            catch(Exception e){
            }
            
            test.stopTest();
            
        }
    }
    
    
    static testMethod void contactNotMatch(){
        objProfile = [SELECT Id FROM Profile WHERE Name = 'System Administrator' Limit 1]; 

        objUserRole = [SELECT Id FROM UserRole WHERE Name LIKE '%Growth%' Limit 1];

        
        objUser = testDataFactory.createAdminUser(objProfile.id, objUserRole.id);
        insert objUser;    
        system.runAs(objUser){
            activateQuoteTrigger();
            
            List<Account> accountList = new List<Account>();
            List<Contact> contactsList = new List<Contact>();
            List<Opportunity> opportunityList = new List<Opportunity>();
            List<SBQQ__Quote__c> quotesList = new List<SBQQ__Quote__c>();
            List<Product2> productsToInsertList = new List<Product2>();
            List<PriceBook2> priceBooksToBeInsertedList = new List<PriceBook2>();
            List<PriceBookEntry> priceBookEntryList = new List<PriceBookEntry>();
            List<SBQQ__QuoteLine__c> quoteLinesToInsert = new List<SBQQ__QuoteLine__c>();
            List<SBQQ__Quote__c> quotesWithContractList = new List<SBQQ__Quote__c>();
            List<Contract> contractsList = new List<Contract>();
            
            
            accountList = TestCPQUtilityClass.createAccounts(2);
            accountList.get(0).Agency_Account__c = true;
            insert accountList ;

            contactsList = TestCPQUtilityClass.createContacts(accountList ,2);
            contactsList.get(0).FirstName  ='Test';
            contactsList.get(0).LastName  ='Contact';
            contactsList.get(0).Email  ='t@test.com';
            insert contactsList; 

            opportunityList = TestCPQUtilityClass.createOpportunities(accountList,1);
            insert opportunityList;
            
            productsToInsertList = TestCPQUtilityClass.createCustomProducts(5);
            insert productsToInsertList;
            
            priceBooksToBeInsertedList = TestCPQUtilityClass.createCustomPriceBooks(2);
            insert priceBooksToBeInsertedList;
            
            TestCPQUtilityClass.createStdPricebookEntries(productsToInsertList,Test.getStandardPricebookId());
            
            priceBookEntryList = TestCPQUtilityClass.createPriceBookEntries(productsToInsertList,priceBooksToBeInsertedList);
            insert priceBookEntryList;
            
            contractsList = TestCPQUtilityClass.createContracts(accountList ,1);
            insert contractsList ;
            
            //SkipAutomationSetting__c customSettingObj = new SkipAutomationSetting__c(Opportunity_Automation__c = true);
           // insert customSettingObj;
            
            quotesList = TestCPQUtilityClass.createQuotes(opportunityList,2);
            
            quotesList.get(0).Legal_Special_Terms__c ='Assignment to Competitor';
            quotesList.get(0).Customer_PO__c = 'Testing';
            quotesList.get(3).Customer_PO__c = 'Testing';
            quotesList.get(1).SBQQ__StartDate__c  = system.today(); 
            quotesList.get(1).SBQQ__SubscriptionTerm__c = 3;
            quotesList.get(2).Agency_Account__c = accountList.get(0).id;  
              Test.startTest();
            insert quotesList;
           
            quotesList.get(2).SBQQ__Type__c='Amendment';
            quotesList.get(2).SBQQ__MasterContract__c = contractsList.get(0).Id;            
            quotesList.get(0).Bill_To_Street__c ='Test';
            quotesList.get(0).Customer_PO__c = 'Test';
            quotesList.get(3).Customer_PO__c = 'Test';
            quotesList.get(0).Billing_Contact_First_Name__c = 'Test Not Match';
            quotesList.get(0).Billing_Contact_Last_Name__c ='Contact Not Match';
            quotesList.get(0).Billing_Email__c = 'tt@test.com'; 
            quotesList.get(1).Bill_To_Contact__c = contactsList.get(1).id;
            quotesList.get(1).Sold_To_Contact__c = contactsList.get(1).id;
            //quotesList.get(0).SBQQ__ListAmount__c = 1000;
            //quotesList.get(1).SBQQ__LineItemCount__c = 1;
            quotesList.get(0).SBQQ__Primary__c = true;
            quotesList.get(3).Agency_Account__c = accountList.get(0).id;           
            
            update quotesList;
            SBQQ__Quote__c sq2 = [select id,Bill_To_Contact__c ,SBQQ__MasterContract__c ,SBQQ__Type__c,Customer_PO__c,Billing_Contact_First_Name__c,Billing_Contact_Last_Name__c,Billing_Email__c    from SBQQ__Quote__c where id =: quotesList.get(2).id];
            // system.assertequals(sq2.SBQQ__Type__c,'Amendment');
            // system.assertnotequals(sq2.SBQQ__MasterContract__c ,null);
             
            SBQQ__Quote__c sq1 = [select id,Bill_To_Contact__c ,Customer_PO__c,Billing_Contact_First_Name__c,Billing_Contact_Last_Name__c,Billing_Email__c    from SBQQ__Quote__c where id =: quotesList.get(0).id];
             //system.assertequals(sq1.Bill_To_Contact__c ,contactsList.get(0).id);
             system.assertequals(sq1.Billing_Contact_Last_Name__c  ,'Contact Not Match');
             system.assertequals(sq1.Billing_Email__c  ,'tt@test.com');
             
            quoteLinesToInsert = TestCPQUtilityClass.createQuoteLines(quotesList,2,productsToInsertList );
            insert quoteLinesToInsert;
            
          
            quotesList.get(1).Bill_To_Contact__c = contactsList.get(0).id;
            quotesList.get(1).Sold_To_Contact__c = contactsList.get(0).id;
            quotesList.get(2).SBQQ__Type__c='Amendment';
            quotesList.get(2).SBQQ__MasterContract__c = contractsList.get(0).Id;
            quotesList.get(3).Agency_Account__c = null; 
            quotesList.get(3).SBQQ__Type__c = 'Renewal'; 
            quotesList.get(1).SBQQ__StartDate__c  = system.today()+1;  
            quotesList.get(1).SBQQ__SubscriptionTerm__c = 5; 
            
            update quotesList;  
            
            contactsList.get(0).FirstName  =  quotesList.get(0).Billing_Contact_First_Name__c ;
            contactsList.get(0).LastName  =quotesList.get(0).Billing_Contact_Last_Name__c;
            contactsList.get(0).Created_By_DS_Write_Back__c  =true;
            contactsList.get(0).Email  =quotesList.get(0).Billing_Email__c;
            update contactsList;
            
            test.stopTest();
            try{        
            delete quotesList;
            }
            catch(Exception e){
            }
            
        }
    }
    
    static testMethod void teststaticQuoteTrigger(){
       Boolean varBool =  staticQuoteTrigger.isFirstTime;
    }
    
    static testMethod void testPopulaeBillingTypeBillingFrequencyForAmend(){
        objProfile = [SELECT Id FROM Profile WHERE Name = 'System Administrator' Limit 1]; 

        objUserRole = [SELECT Id FROM UserRole WHERE Name LIKE '%Growth%' Limit 1];

        
        objUser = testDataFactory.createAdminUser(objProfile.id, objUserRole.id);
        objUser.Admin_Edit__c = true;
        insert objUser;    
        system.runAs(objUser){
            activateQuoteTrigger();
            List<Account> accountList = new List<Account>();
            List<Contact> contactsList = new List<Contact>();
            List<Opportunity> opportunityList = new List<Opportunity>();
            //List<SBQQ__Quote__c> quotesList = new List<SBQQ__Quote__c>();
            List<Product2> productsToInsertList = new List<Product2>();
            List<PriceBook2> priceBooksToBeInsertedList = new List<PriceBook2>();
            List<PriceBookEntry> priceBookEntryList = new List<PriceBookEntry>();
            List<SBQQ__QuoteLine__c> quoteLinesToInsert = new List<SBQQ__QuoteLine__c>();
            List<SBQQ__Quote__c> quotesWithContractList = new List<SBQQ__Quote__c>();
            List<Contract> contractsList = new List<Contract>();
            
            
            accountList = TestCPQUtilityClass.createAccounts(2);
            accountList.get(0).Agency_Account__c = true;
            insert accountList ;

            contactsList = TestCPQUtilityClass.createContacts(accountList ,2);
            contactsList.get(0).FirstName  ='Test';
            contactsList.get(0).LastName  ='Contact';
            contactsList.get(0).Email  ='t@test.com';
            insert contactsList; 

            opportunityList = TestCPQUtilityClass.createOpportunities(accountList,1);
            insert opportunityList;
            
            productsToInsertList = TestCPQUtilityClass.createCustomProducts(5);
            insert productsToInsertList;
            
            priceBooksToBeInsertedList = TestCPQUtilityClass.createCustomPriceBooks(2);
            insert priceBooksToBeInsertedList;
            
            TestCPQUtilityClass.createStdPricebookEntries(productsToInsertList,Test.getStandardPricebookId());
            
            priceBookEntryList = TestCPQUtilityClass.createPriceBookEntries(productsToInsertList,priceBooksToBeInsertedList);
            insert priceBookEntryList;
            
            contractsList = TestCPQUtilityClass.createContracts(accountList ,1);

            insert contractsList ;
            system.debug('contractsList +'+contractsList );
            
            //SkipAutomationSetting__c customSettingObj = new SkipAutomationSetting__c(Opportunity_Automation__c = true);
            //insert customSettingObj;
            
            quotesWithContractList = TestCPQUtilityClass.createQuotesWithMasterContracts(opportunityList,2,contractsList.get(0));
            quotesWithContractList.get(0).SBQQ__Type__c = 'Amendment';
            quotesWithContractList.get(0).SBQQ__MasterContract__c = contractsList.get(0).Id;       
            quotesWithContractList.get(0).SBQQ__BillingFrequency__c  = 'One Time';
            quotesWithContractList.get(0).Billing_Type__c = 'Advance';
            quotesWithContractList.get(1).SBQQ__Type__c = 'Amendment';
            quotesWithContractList.get(1).SBQQ__MasterContract__c = contractsList.get(0).Id;
            quotesWithContractList.get(1).Bill_To_Contact__c = contactsList.get(0).id;
            quotesWithContractList.get(1).Sold_To_Contact__c = contactsList.get(0).id;
            insert quotesWithContractList ;
            
            SBQQ__Quote__c sq2 = [select id,Bill_To_Contact__c ,SBQQ__MasterContract__c ,SBQQ__Type__c,Customer_PO__c,Billing_Contact_First_Name__c,Billing_Contact_Last_Name__c,Billing_Email__c    from SBQQ__Quote__c where id =: quotesWithContractList.get(0).id];
            system.assertequals(sq2.SBQQ__Type__c,'Amendment');
             
           //system.assertEquals(sq2.SBQQ__MasterContract__c ,null);

            
        }
    }  
    
      private static void activateQuoteTrigger() {
        SkipAutomationSetting__c customSettingObj = new SkipAutomationSetting__c(SBQQ_Quote_Automation__c = true);
        insert customSettingObj;
      }
    
    static testMethod void createGroupRecordsTest(){
        objProfile = [SELECT Id FROM Profile WHERE Name = 'System Administrator' Limit 1]; 

        objUserRole = [SELECT Id FROM UserRole WHERE Name LIKE '%Growth%' Limit 1];

        
        objUser = testDataFactory.createAdminUser(objProfile.id, objUserRole.id);
        insert objUser;    
        system.runAs(objUser){
            activateQuoteTrigger();
            
            List<Account> accountList = new List<Account>();
            List<Contact> contactsList = new List<Contact>();
            List<Opportunity> opportunityList = new List<Opportunity>();
            List<SBQQ__Quote__c> quotesList = new List<SBQQ__Quote__c>();
            List<Product2> productsToInsertList = new List<Product2>();
            List<OpportunityLineItem> OpportunityLineItemList = new List<OpportunityLineItem>();
            List<PriceBook2> priceBooksToBeInsertedList = new List<PriceBook2>();
            List<PriceBookEntry> StdpriceBookEntryList = new list <PriceBookEntry>();
            List<PriceBookEntry> priceBookEntryList = new List<PriceBookEntry>();
            List<SBQQ__QuoteLine__c> quoteLinesToInsert = new List<SBQQ__QuoteLine__c>();
            List<SBQQ__Quote__c> quotesWithContractList = new List<SBQQ__Quote__c>();
            List<Contract> contractsList = new List<Contract>();
            
            
            accountList = TestCPQUtilityClass.createAccounts(1);
            accountList.get(0).Agency_Account__c = true;
            insert accountList ;

            contactsList = TestCPQUtilityClass.createContacts(accountList ,1);
            contactsList.get(0).FirstName  ='Test';
            contactsList.get(0).LastName  ='Contact';
            contactsList.get(0).Email  ='t@test.com';
            insert contactsList;

            
            /*opportunity oppObject = new opportunity();
            oppObject.AccountId= accountList.get(0).id;
            oppObject.Name = 'Test Opportunity';
            oppObject.StageName = 'Negotiate';
            oppObject.Pricebook2Id = priceBookEntryList.get(0).id;
            oppObject.CloseDate= System.Today().AddDays(1);
            insert oppObject;*/
            
            productsToInsertList = TestCPQUtilityClass.createCustomProducts(5);
            productsToInsertList[0].CanUseRevenueSchedule = true;
            productsToInsertList[1].CanUseRevenueSchedule = true;
            productsToInsertList[2].CanUseRevenueSchedule = true;
            productsToInsertList[3].CanUseRevenueSchedule = true;
            productsToInsertList[4].CanUseRevenueSchedule = true;
           
            insert productsToInsertList;
            
            priceBooksToBeInsertedList = TestCPQUtilityClass.createCustomPriceBooks(1);
            insert priceBooksToBeInsertedList;
            system.assertEquals(1, priceBooksToBeInsertedList.size());
            
            opportunityList = TestCPQUtilityClass.createOpportunities(accountList,1);
            opportunityList[0].priceBook2Id = priceBooksToBeInsertedList[0].Id;
            insert opportunityList;
                        opportunityList[0].priceBook2Id = priceBooksToBeInsertedList[0].Id;
            update  opportunityList;
                        system.assertEquals(1, opportunityList.size());

            system.debug('opportunityList===='+opportunityList);
            TestCPQUtilityClass.createStdPricebookEntries(productsToInsertList,Test.getStandardPricebookId());
            priceBookEntryList = TestCPQUtilityClass.createPriceBookEntries(productsToInsertList,priceBooksToBeInsertedList);
                                    system.assertEquals(5, priceBookEntryList.size());

            insert priceBookEntryList;
            system.debug('priceBookEntryList===='+priceBookEntryList);
            //system.assertEquals(1, 2); 
            OpportunityLineItemList = TestCPQUtilityClass.createOppLineItems(opportunityList, priceBookEntryList);
                        System.debug('OpportunityLineItemList=========================='+OpportunityLineItemList);

            OpportunityLineItemList.get(0).Delivery_Mode__c = 'Media will be delivered both on and off GD';
            OpportunityLineItemList.get(0).Duration_in_Months__c = 2;
            System.debug('OpportunityLineItemList=========================='+OpportunityLineItemList);
            insert OpportunityLineItemList;
            Test.startTest();
            contractsList = TestCPQUtilityClass.createContracts(accountList ,1);
            contractsList.get(0).Has_Grouping__c = true;
            insert contractsList;
            //SkipAutomationSetting__c customSettingObj = new SkipAutomationSetting__c(Opportunity_Automation__c = true);
            // insert customSettingObj;
            
            
                            
            
            
            quotesList = TestCPQUtilityClass.createQuotes(opportunityList,2);
            
            quotesList.get(0).SBQQ__LineItemsGrouped__c=true;
            quotesList.get(0).SBQQ__MasterContract__c = contractsList.get(0).Id;            
            quotesList.get(1).Renewed_Contract__c = contractsList.get(0).Id; 
            quotesList.get(1).SBQQ__LineItemsGrouped__c=true;
            insert quotesList;
            
            List<SBQQ__QuoteLineGroup__c> createQuoteLineGroup  = TestCPQUtilityClass.createQuoteLineGroup(accountList,quotesList,2);
            createQuoteLineGroup.get(0).SBQQ__Quote__c = quotesList.get(0).id;
            createQuoteLineGroup.get(1).SBQQ__Quote__c = quotesList.get(0).id;
            insert createQuoteLineGroup ;
            
            List<SBQQ__Subscription__c> subRecs = TestCPQUtilityClass.createSubscriptions(contractsList,6);
            subRecs.get(0).Quote_Line_Group__c = createQuoteLineGroup.get(0).id;
            insert subRecs;
            
            OpportunityLineItemSchedule ObjOpportunityLineItemSchedule = new OpportunityLineItemSchedule();
            ObjOpportunityLineItemSchedule.ScheduleDate = System.Today().AddDays(1);
            ObjOpportunityLineItemSchedule.OpportunityLineItemId = OpportunityLineItemList.get(0).id;
             ObjOpportunityLineItemSchedule.Type='Revenue';
            ObjOpportunityLineItemSchedule.Revenue=300;

            insert ObjOpportunityLineItemSchedule;
            
            quotesList.clear();
            quotesList = TestCPQUtilityClass.createQuotes(opportunityList,2);
            quotesList.get(0).SBQQ__LineItemsGrouped__c=true;
            quotesList.get(0).SBQQ__MasterContract__c = contractsList.get(0).Id;            
            quotesList.get(1).Renewed_Contract__c = contractsList.get(0).Id; 
            quotesList.get(1).SBQQ__LineItemsGrouped__c=true;
            insert quotesList;

            
             test.stopTest();
            try{        
            delete quotesList;
            }
            catch(Exception e){
            }
            
            
        }
    }
        private static void PopulateExchangeRateTest() {
            List<Account> accountList = new List<Account>();
            List<Contact> contactsList = new List<Contact>();
            List<Opportunity> opportunityList = new List<Opportunity>();
            List<SBQQ__Quote__c> quotesList = new List<SBQQ__Quote__c>();
             Map<Id,Opportunity> opportunityMap = new Map<Id,Opportunity>();
            
            DatedConversionRate objDatedConversionRate= new DatedConversionRate();
            objDatedConversionRate.StartDate= Date.valueOf(System.now())-1; 
           // objDatedConversionRate.IsoCode = 'USD';
            objDatedConversionRate.IsoCode='EUR';
      
            accountList = TestCPQUtilityClass.createAccounts(2);
            accountList.get(0).Agency_Account__c = true;
            insert accountList ;

            contactsList = TestCPQUtilityClass.createContacts(accountList ,2);
            contactsList.get(0).FirstName  ='Test';
            contactsList.get(0).LastName  ='Contact';
            contactsList.get(0).Email  ='t@test.com';
            insert contactsList; 

            opportunityList = TestCPQUtilityClass.createOpportunities(accountList,1);
            opportunityList[0].CurrencyIsoCode='EUR';
            insert opportunityList;
            
            quotesList = TestCPQUtilityClass.createQuotes(opportunityList,2);
            
            quotesList.get(0).Quote_Dated_Exchange_Rate__c= objDatedConversionRate.ConversionRate;
            quotesList.get(0).CurrencyIsoCode='EUR';
            insert quotesList;
            SBQQ__QuoteLineGroup__c quotelinegroupObj = new SBQQ__QuoteLineGroup__c();
            quotelinegroupObj.CurrencyIsoCode = 'EUR';
            insert quotelinegroupObj;
            
            SBQQ__Subscription__c subscriptionObj = new SBQQ__Subscription__c();
            subscriptionObj.Quote_Line_Group__c = quotelinegroupObj.Id;
            insert subscriptionObj;
                                         
      }
    
    // updateamendFields method
    static testMethod void TestUpdateAmendFields(){
        objProfile = [SELECT Id FROM Profile WHERE Name = 'System Administrator' Limit 1]; 

        objUserRole = [SELECT Id FROM UserRole WHERE Name LIKE '%Growth%' Limit 1];

        
        objUser = testDataFactory.createAdminUser(objProfile.id, objUserRole.id);
        objUser.Admin_Edit__c = true;
        insert objUser;    
        system.runAs(objUser){
            activateQuoteTrigger();
            List<Account> accountList = new List<Account>();
            List<Contact> contactsList = new List<Contact>();
            List<Opportunity> opportunityList = new List<Opportunity>();
            //List<SBQQ__Quote__c> quotesList = new List<SBQQ__Quote__c>();
            List<Product2> productsToInsertList = new List<Product2>();
            List<PriceBook2> priceBooksToBeInsertedList = new List<PriceBook2>();
            List<PriceBookEntry> priceBookEntryList = new List<PriceBookEntry>();
            List<SBQQ__QuoteLine__c> quoteLinesToInsert = new List<SBQQ__QuoteLine__c>();
            List<SBQQ__Quote__c> quotesWithContractList = new List<SBQQ__Quote__c>();
            List<Contract> contractsList = new List<Contract>();
            
            
            accountList = TestCPQUtilityClass.createAccounts(2);
            accountList.get(0).Agency_Account__c = true;
            insert accountList ;

            contactsList = TestCPQUtilityClass.createContacts(accountList ,2);
            contactsList.get(0).FirstName  ='Test';
            contactsList.get(0).LastName  ='Contact';
            contactsList.get(0).Email  ='t@test.com';
            insert contactsList; 

            opportunityList = TestCPQUtilityClass.createOpportunities(accountList,1);
            insert opportunityList;
            
            productsToInsertList = TestCPQUtilityClass.createCustomProducts(5);
            insert productsToInsertList;
            
            priceBooksToBeInsertedList = TestCPQUtilityClass.createCustomPriceBooks(2);
            insert priceBooksToBeInsertedList;
            
            TestCPQUtilityClass.createStdPricebookEntries(productsToInsertList,Test.getStandardPricebookId());
            
            priceBookEntryList = TestCPQUtilityClass.createPriceBookEntries(productsToInsertList,priceBooksToBeInsertedList);
            insert priceBookEntryList;
            
            contractsList = TestCPQUtilityClass.createContracts(accountList ,1);

            insert contractsList ;
            system.debug('contractsList +'+contractsList );
            //add contracts to the opportunity
            opportunityList[0].SBQQ__RenewedContract__c=contractsList[0].id;
            update opportunityList[0];
            //SkipAutomationSetting__c customSettingObj = new SkipAutomationSetting__c(Opportunity_Automation__c = true);
            //insert customSettingObj;
            
            quotesWithContractList = TestCPQUtilityClass.createQuotesWithMasterContracts(opportunityList,2,contractsList.get(0));
            quotesWithContractList.get(0).SBQQ__Type__c = 'Amendment';
            quotesWithContractList.get(0).Legal_Special_Terms__c='Negotiated Services Terms';
            quotesWithContractList.get(0).SBQQ__MasterContract__c = contractsList.get(0).Id;       
            quotesWithContractList.get(0).SBQQ__BillingFrequency__c  = 'One Time';
            quotesWithContractList.get(0).Billing_Type__c = 'Advance';
            quotesWithContractList.get(1).SBQQ__Type__c = 'Renewal';
            quotesWithContractList.get(1).SBQQ__MasterContract__c = contractsList.get(0).Id;
            quotesWithContractList.get(1).Bill_To_Contact__c = contactsList.get(0).id;
            quotesWithContractList.get(1).Sold_To_Contact__c = contactsList.get(0).id;
            quotesWithContractList.get(1).Legal_Special_Terms__c='Negotiated Services Terms';
            insert quotesWithContractList ;
            
            SBQQ__Quote__c sq2 = [select id,Bill_To_Contact__c ,SBQQ__MasterContract__c ,SBQQ__Type__c,Customer_PO__c,Billing_Contact_First_Name__c,Billing_Contact_Last_Name__c,Billing_Email__c    from SBQQ__Quote__c where id =: quotesWithContractList.get(0).id];
 
            
        }
    }  

    static testMethod void contactNotMatchNegative(){
        objProfile = [SELECT Id FROM Profile WHERE Name = 'System Administrator' Limit 1]; 

        objUserRole = [SELECT Id FROM UserRole WHERE Name LIKE '%Growth%' Limit 1];

        
        objUser = testDataFactory.createAdminUser(objProfile.id, objUserRole.id);
        insert objUser;    
        system.runAs(objUser){
            activateQuoteTrigger();
            
            List<Account> accountList = new List<Account>();
            List<Contact> contactsList = new List<Contact>();
            List<Opportunity> opportunityList = new List<Opportunity>();
            List<SBQQ__Quote__c> quotesList = new List<SBQQ__Quote__c>();
            List<Product2> productsToInsertList = new List<Product2>();
            List<PriceBook2> priceBooksToBeInsertedList = new List<PriceBook2>();
            List<PriceBookEntry> priceBookEntryList = new List<PriceBookEntry>();
            List<SBQQ__QuoteLine__c> quoteLinesToInsert = new List<SBQQ__QuoteLine__c>();
            List<SBQQ__Quote__c> quotesWithContractList = new List<SBQQ__Quote__c>();
            List<Contract> contractsList = new List<Contract>();
            
            
            accountList = TestCPQUtilityClass.createAccounts(2);
            accountList.get(0).Agency_Account__c = false;
            insert accountList ;

            contactsList = TestCPQUtilityClass.createContacts(accountList ,2);
            contactsList.get(0).FirstName  ='Test';
            contactsList.get(0).LastName  ='Contact';
            contactsList.get(0).Email  ='t@test.com';
            insert contactsList; 

            opportunityList = TestCPQUtilityClass.createOpportunities(accountList,1);
            insert opportunityList;
            
            productsToInsertList = TestCPQUtilityClass.createCustomProducts(5);
            insert productsToInsertList;
            
            priceBooksToBeInsertedList = TestCPQUtilityClass.createCustomPriceBooks(2);
            insert priceBooksToBeInsertedList;
            
            TestCPQUtilityClass.createStdPricebookEntries(productsToInsertList,Test.getStandardPricebookId());
            
            priceBookEntryList = TestCPQUtilityClass.createPriceBookEntries(productsToInsertList,priceBooksToBeInsertedList);
            insert priceBookEntryList;
            
            contractsList = TestCPQUtilityClass.createContracts(accountList ,1);
            insert contractsList ;
            
            //SkipAutomationSetting__c customSettingObj = new SkipAutomationSetting__c(Opportunity_Automation__c = true);
           // insert customSettingObj;
            
            quotesList = TestCPQUtilityClass.createQuotes(opportunityList,2);
            
            quotesList.get(0).Legal_Special_Terms__c ='Assignment to Competitor';
            quotesList.get(0).Customer_PO__c = 'Testing';
            quotesList.get(0).DocusignCompletedDate__c=null;
            quotesList.get(1).DocusignCompletedDate__c=null;
            quotesList.get(3).Customer_PO__c = 'Testing';
            quotesList.get(1).SBQQ__StartDate__c  = system.today(); 
            quotesList.get(1).SBQQ__SubscriptionTerm__c = 3;
            quotesList.get(2).Agency_Account__c = accountList.get(0).id;  
              Test.startTest();
            insert quotesList;
           
            quotesList.get(2).SBQQ__Type__c='Amendment';
            quotesList.get(2).SBQQ__MasterContract__c = contractsList.get(0).Id;            
            quotesList.get(0).Bill_To_Street__c ='Test';
            quotesList.get(0).Customer_PO__c = 'Test';
            quotesList.get(3).Customer_PO__c = 'Test';
            quotesList.get(0).Billing_Contact_First_Name__c = 'Test Not Match';
            quotesList.get(0).Billing_Contact_Last_Name__c ='Contact Not Match';
            quotesList.get(0).Billing_Email__c = 'tt@test.com'; 
            quotesList.get(1).Bill_To_Contact__c = contactsList.get(1).id;
            quotesList.get(1).Sold_To_Contact__c = contactsList.get(1).id;
            //quotesList.get(0).SBQQ__ListAmount__c = 1000;
            //quotesList.get(1).SBQQ__LineItemCount__c = 1;
            quotesList.get(0).SBQQ__Primary__c = true;
            quotesList.get(3).Agency_Account__c = accountList.get(0).id;           
            
            update quotesList;
            SBQQ__Quote__c sq2 = [select id,Bill_To_Contact__c ,SBQQ__MasterContract__c ,SBQQ__Type__c,Customer_PO__c,Billing_Contact_First_Name__c,Billing_Contact_Last_Name__c,Billing_Email__c    from SBQQ__Quote__c where id =: quotesList.get(2).id];
            // system.assertequals(sq2.SBQQ__Type__c,'Amendment');
            // system.assertnotequals(sq2.SBQQ__MasterContract__c ,null);
             
            SBQQ__Quote__c sq1 = [select id,Bill_To_Contact__c ,Customer_PO__c,Billing_Contact_First_Name__c,Billing_Contact_Last_Name__c,Billing_Email__c    from SBQQ__Quote__c where id =: quotesList.get(0).id];
             //system.assertequals(sq1.Bill_To_Contact__c ,contactsList.get(0).id);
             system.assertequals(sq1.Billing_Contact_Last_Name__c  ,'Contact Not Match');
             system.assertequals(sq1.Billing_Email__c  ,'tt@test.com');
             
            quoteLinesToInsert = TestCPQUtilityClass.createQuoteLines(quotesList,2,productsToInsertList );
            insert quoteLinesToInsert;
            
          
            quotesList.get(1).Bill_To_Contact__c = contactsList.get(0).id;
            quotesList.get(1).Sold_To_Contact__c = contactsList.get(0).id;
            quotesList.get(2).SBQQ__Type__c='Amendment';
            quotesList.get(2).SBQQ__MasterContract__c = contractsList.get(0).Id;
            quotesList.get(3).Agency_Account__c = null; 
            quotesList.get(3).SBQQ__Type__c = 'Renewal'; 
            quotesList.get(1).SBQQ__StartDate__c  = system.today()+1;  
            quotesList.get(1).SBQQ__SubscriptionTerm__c = 5; 
            quotesList.get(1).DocusignCompletedDate__c=system.today();
            quotesList.get(0).DocusignCompletedDate__c=system.today();
            update quotesList;  
            system.debug('docusignCompletedDate'+quotesList.get(0).DocusignCompletedDate__c);
            contactsList.get(0).FirstName  =  quotesList.get(0).Billing_Contact_First_Name__c ;
            contactsList.get(0).LastName  =quotesList.get(0).Billing_Contact_Last_Name__c;
            contactsList.get(0).Created_By_DS_Write_Back__c  =true;
            contactsList.get(0).Email  =quotesList.get(0).Billing_Email__c;
            update contactsList;
            
            test.stopTest();
            try{        
            delete quotesList;
            }
            catch(Exception e){
            }
            
        }
    }
    
    
}