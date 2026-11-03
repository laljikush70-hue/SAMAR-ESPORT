const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid";
const BOB_UID = "bob_uid";
const ADMIN_UID = "admin_uid";
const ADMIN_EMAIL = "laljikush70@gmail.com";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
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

test("Unauthenticated user: cannot read tournaments", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("tournaments").get());
});

test("Authenticated user: can read tournaments list", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("tournaments").get());
});

test("Non-admin: cannot create a tournament", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("tournaments").doc("tourney_1").set({
      id: "tourney_1",
      name: "Free Fire Mega Cup",
      category: "SOLO",
      matchDateTime: "2026-11-10 18:00",
      entryFee: 50,
      prizePool: 2000,
      maxSlots: 48,
      status: "UPCOMING",
      createdAt: new Date(),
    })
  );
});

test("Admin: can create a tournament", async () => {
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, { email: ADMIN_EMAIL, email_verified: true }).firestore();
  await assertSucceeds(
    adminDb.collection("tournaments").doc("tourney_1").set({
      id: "tourney_1",
      name: "Free Fire Mega Cup",
      category: "SOLO",
      matchDateTime: "2026-11-10 18:00",
      entryFee: 50,
      prizePool: 2000,
      maxSlots: 48,
      status: "UPCOMING",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: can register themselves for a tournament", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("registrations").doc("reg_1").set({
      id: "reg_1",
      tournamentId: "tourney_1",
      userId: ALICE_UID,
      userName: "Alice Gaming",
      playerUid: "FF12345678",
      inGameName: "AlicePro",
      category: "SOLO",
      entryFeePaid: 50,
      status: "REGISTERED",
      joinedAt: new Date(),
    })
  );
});

test("Cross-user spoofing: Alice cannot register Bob", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("registrations").doc("reg_2").set({
      id: "reg_2",
      tournamentId: "tourney_1",
      userId: BOB_UID,
      userName: "Bob Gaming",
      playerUid: "FF87654321",
      inGameName: "BobPro",
      category: "SOLO",
      entryFeePaid: 50,
      status: "REGISTERED",
      joinedAt: new Date(),
    })
  );
});

test("Wallet: Alice can read her own wallet but not Bob's", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("wallets").doc(BOB_UID).set({
      userId: BOB_UID,
      coinBalance: 500,
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("wallets").doc(BOB_UID).get());
});

test("Transactions: cannot be deleted by anyone", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("transactions").doc("tx_1").set({
      id: "tx_1",
      userId: ALICE_UID,
      type: "CREDIT",
      category: "COIN_PURCHASE",
      amount: 100,
      title: "Add 100 Coins",
      status: "SUCCESS",
      createdAt: new Date(),
    });
  });

  const adminDb = testEnv.authenticatedContext(ADMIN_UID, { email: ADMIN_EMAIL, email_verified: true }).firestore();
  await assertFails(adminDb.collection("transactions").doc("tx_1").delete());
});
