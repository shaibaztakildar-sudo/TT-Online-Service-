const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

const PROJECT_ID = "demo-no-project";
const DATABASE_ID = "ai-studio-android-ttonline-047e739f-f599-4f8d-a2bb-831e9309b220";

let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules: fs.readFileSync("firestore.rules", "utf8"),
      host: "127.0.0.1",
      port: 8085
    }
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("unauthenticated user cannot read applications", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore(DATABASE_ID);
  await assertFails(unauthDb.collection("applications").get());
});

test("authenticated customer can create and read own user document", async () => {
  const authDb = testEnv.authenticatedContext("user123", { email: "customer@example.com" }).firestore(DATABASE_ID);
  const now = new Date();
  await assertSucceeds(
    authDb.collection("users").doc("user123").set({
      id: "user123",
      email: "customer@example.com",
      name: "Customer Name",
      phone: "9876543210",
      role: "customer",
      createdAt: now,
      updatedAt: now
    })
  );

  const doc = await assertSucceeds(authDb.collection("users").doc("user123").get());
});

test("authenticated customer cannot read other customer user document", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const adminDb = context.firestore(DATABASE_ID);
    await adminDb.collection("users").doc("user456").set({
      id: "user456",
      email: "other@example.com",
      name: "Other User",
      role: "customer",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const authDb = testEnv.authenticatedContext("user123").firestore(DATABASE_ID);
  await assertFails(authDb.collection("users").doc("user456").get());
});

test("customer can create application for themselves", async () => {
  const authDb = testEnv.authenticatedContext("user123").firestore(DATABASE_ID);
  const now = new Date();
  await assertSucceeds(
    authDb.collection("applications").doc("TT-2026-0001").set({
      applicationId: "TT-2026-0001",
      userId: "user123",
      customerName: "Customer Name",
      serviceId: "pan_card",
      serviceName: "PAN Card Assistance",
      status: "Pending",
      paymentStatus: "Pending",
      consentGiven: true,
      createdAt: now,
      updatedAt: now
    })
  );
});

test("customer cannot read another customer's application", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const adminDb = context.firestore(DATABASE_ID);
    await adminDb.collection("applications").doc("TT-2026-0002").set({
      applicationId: "TT-2026-0002",
      userId: "user456",
      customerName: "Other User",
      serviceId: "pan_card",
      serviceName: "PAN Card Assistance",
      status: "Pending",
      paymentStatus: "Pending",
      consentGiven: true,
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const authDb = testEnv.authenticatedContext("user123").firestore(DATABASE_ID);
  await assertFails(authDb.collection("applications").doc("TT-2026-0002").get());
});

test("admin can read all applications", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const adminDb = context.firestore(DATABASE_ID);
    await adminDb.collection("users").doc("admin123").set({
      id: "admin123",
      email: "shaibaztakildar@gmail.com",
      name: "Owner Admin",
      role: "admin",
      createdAt: new Date(),
      updatedAt: new Date()
    });
    await adminDb.collection("applications").doc("TT-2026-0003").set({
      applicationId: "TT-2026-0003",
      userId: "user789",
      customerName: "Customer 789",
      serviceId: "voter_id",
      serviceName: "Voter ID Assistance",
      status: "Pending",
      paymentStatus: "Pending",
      consentGiven: true,
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const adminAuthDb = testEnv.authenticatedContext("admin123", { email: "shaibaztakildar@gmail.com" }).firestore(DATABASE_ID);
  await assertSucceeds(adminAuthDb.collection("applications").doc("TT-2026-0003").get());
});
