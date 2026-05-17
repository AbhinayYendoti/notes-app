# -*- coding: utf-8 -*-
import os, json, sys, uuid, urllib.error, urllib.request

try:
    import requests
except ModuleNotFoundError:
    class Response:
        def __init__(self, status_code, body, headers):
            self.status_code = status_code
            self.text = body
            self.headers = {k.lower(): v for k, v in headers.items()}

        def json(self):
            return json.loads(self.text)

    class RequestsFallback:
        @staticmethod
        def request(method, url, headers=None, json=None, data=None):
            body = data
            if json is not None:
                body = globals()["json"].dumps(json).encode("utf-8")
            elif isinstance(data, str):
                body = data.encode("utf-8")
            req = urllib.request.Request(url, data=body, headers=headers or {}, method=method)
            try:
                with urllib.request.urlopen(req) as res:
                    text = res.read().decode("utf-8")
                    return Response(res.status, text, dict(res.headers))
            except urllib.error.HTTPError as ex:
                text = ex.read().decode("utf-8")
                return Response(ex.code, text, dict(ex.headers))

        @classmethod
        def get(cls, url, headers=None):
            return cls.request("GET", url, headers=headers)

        @classmethod
        def post(cls, url, headers=None, json=None, data=None):
            return cls.request("POST", url, headers=headers, json=json, data=data)

        @classmethod
        def put(cls, url, headers=None, json=None):
            return cls.request("PUT", url, headers=headers, json=json)

        @classmethod
        def patch(cls, url, headers=None):
            return cls.request("PATCH", url, headers=headers)

        @classmethod
        def delete(cls, url, headers=None):
            return cls.request("DELETE", url, headers=headers)

    requests = RequestsFallback

BASE = os.environ.get("BASE", "http://localhost:8080")
PASS = 0; FAIL = 0

def check(name, condition, actual=""):
    global PASS, FAIL
    if condition: PASS += 1; print(f"✅ PASS | {name}")
    else: FAIL += 1; print(f"❌ FAIL | {name} | Got: {str(actual)[:120]}")

def h(token=None):
    headers = {"Content-Type": "application/json"}
    if token: headers["Authorization"] = f"Bearer {token}"
    return headers

email1 = f"u1_{uuid.uuid4().hex[:6]}@test.com"
email2 = f"u2_{uuid.uuid4().hex[:6]}@test.com"

# PUBLIC
r = requests.get(f"{BASE}/about"); check("GET /about → 200", r.status_code==200, r.text[:80])
check("GET /about → has name", "name" in r.json(), r.text[:80])
check("GET /about → has my features", "my features" in r.json(), r.text[:80])
r = requests.get(f"{BASE}/openapi.json"); check("GET /openapi.json → 200", r.status_code==200, r.text[:80])
check("GET /openapi.json → has openapi field", "openapi" in r.json(), r.text[:80])

# REGISTER
r = requests.post(f"{BASE}/register", headers=h(), json={"email":email1,"password":"password123"}); check("POST /register → 201", r.status_code==201, r.text)
r = requests.post(f"{BASE}/register", headers=h(), json={"email":email2,"password":"password123"}); check("POST /register user2 → 201", r.status_code==201, r.text)
r = requests.post(f"{BASE}/register", headers=h(), json={"email":email1,"password":"password123"}); check("POST /register duplicate → 409", r.status_code==409, r.text)
r = requests.post(f"{BASE}/register", headers=h(), json={"email":"notanemail","password":"password123"}); check("POST /register invalid email → 400", r.status_code==400, r.text)
r = requests.post(f"{BASE}/register", headers=h(), json={"email":f"x{uuid.uuid4().hex[:4]}@t.com","password":"short"}); check("POST /register short password → 400", r.status_code==400, r.text)
r = requests.post(f"{BASE}/register", headers=h(), json={"password":"password123"}); check("POST /register missing email → 400", r.status_code==400, r.text)
r = requests.post(f"{BASE}/register", headers={"Content-Type":"application/json"}, data="{bad"); check("POST /register malformed JSON → 400", r.status_code==400, r.text)

# LOGIN
r = requests.post(f"{BASE}/login", headers=h(), json={"email":email1,"password":"password123"}); check("POST /login → 200", r.status_code==200, r.text); token1 = r.json().get("access_token","")
r = requests.post(f"{BASE}/login", headers=h(), json={"email":email2,"password":"password123"}); check("POST /login user2 → 200", r.status_code==200, r.text); token2 = r.json().get("access_token","")
r = requests.post(f"{BASE}/login", headers=h(), json={"email":email1,"password":"wrong"}); check("POST /login wrong password → 401", r.status_code==401, r.text); check("POST /login → generic message", "Invalid email or password" in r.text, r.text)
r = requests.post(f"{BASE}/login", headers=h(), json={"email":"nobody@x.com","password":"password123"}); check("POST /login wrong email → 401", r.status_code==401, r.text)

# NOTES AUTH
r = requests.get(f"{BASE}/notes"); check("GET /notes no auth → 401", r.status_code==401, r.text); check("GET /notes no auth → JSON not HTML", r.headers.get("content-type","").startswith("application/json"), r.headers.get("content-type"))

# CREATE
r = requests.post(f"{BASE}/notes", headers=h(token1), json={"title":"My First Note","content":"First note content here."}); check("POST /notes → 201", r.status_code==201, r.text); note1_id = r.json().get("id","")
r = requests.post(f"{BASE}/notes", headers=h(token1), json={"title":"Second Note","content":"Second note content here."}); check("POST /notes second → 201", r.status_code==201, r.text); note2_id = r.json().get("id","")
r = requests.get(f"{BASE}/notes/{note2_id}/history", headers=h(token1)); check("GET untouched note history → 200 []", r.status_code==200 and r.json()==[], r.text)
r = requests.post(f"{BASE}/notes", headers=h(token1), json={"title":"   ","content":"content"}); check("POST /notes blank title → 400", r.status_code==400, r.text)
r = requests.post(f"{BASE}/notes", headers=h(token1), json={"title":"title","content":"   "}); check("POST /notes blank content → 400", r.status_code==400, r.text)
r = requests.post(f"{BASE}/notes", headers=h(token1), json={"content":"content"}); check("POST /notes missing title → 400", r.status_code==400, r.text)

# GET
r = requests.get(f"{BASE}/notes", headers=h(token1)); check("GET /notes → 200", r.status_code==200, r.text[:80]); check("GET /notes → list ≥2 items", isinstance(r.json(),list) and len(r.json())>=2, r.text[:80])
r = requests.get(f"{BASE}/notes/{note1_id}", headers=h(token1)); check("GET /notes/{id} owner → 200", r.status_code==200, r.text); check("GET /notes/{id} → correct title", r.json().get("title")=="My First Note", r.text)
check("GET /notes/{id} → snake case timestamps", "created_at" in r.json() and "updated_at" in r.json(), r.text)
r = requests.get(f"{BASE}/notes/{note1_id}", headers=h(token2)); check("GET /notes/{id} other user → 403 NOT 404", r.status_code==403, r.text)
r = requests.get(f"{BASE}/notes/not-a-uuid", headers=h(token1)); check("GET /notes/invalid-uuid → 400", r.status_code==400, r.text)
r = requests.get(f"{BASE}/notes/{uuid.uuid4()}", headers=h(token1)); check("GET /notes/non-existent → 404", r.status_code==404, r.text)

# UPDATE
r = requests.put(f"{BASE}/notes/{note1_id}", headers=h(token1), json={"title":"Updated Title","content":"Updated content."}); check("PUT /notes/{id} owner → 200", r.status_code==200, r.text); check("PUT /notes/{id} → title updated", r.json().get("title")=="Updated Title", r.text)
r = requests.put(f"{BASE}/notes/{note1_id}", headers=h(token1), json={"title":"Updated Title V2","content":"Updated content V2."}); check("PUT /notes/{id} second update → 200", r.status_code==200, r.text)
r = requests.get(f"{BASE}/notes/{note1_id}/history", headers=h(token1)); history = r.json() if r.status_code==200 else []
check("GET history after two updates → 2 entries", r.status_code==200 and len(history)==2, r.text)
check("GET history → newest-first", len(history)==2 and history[0].get("versionNumber")==2 and history[1].get("versionNumber")==1, history)
oldest_version_id = history[1].get("id") if len(history)==2 else ""
r = requests.get(f"{BASE}/notes/{note2_id}/history/{oldest_version_id}", headers=h(token1)); check("GET version under wrong note → 404", r.status_code==404, r.text)
r = requests.post(f"{BASE}/notes/{note2_id}/history/{oldest_version_id}/restore", headers=h(token1)); check("POST restore under wrong note → 404", r.status_code==404, r.text)
r = requests.get(f"{BASE}/notes/{note1_id}/history/{oldest_version_id}", headers=h(token1)); check("GET history version → old content", r.status_code==200 and r.json().get("content")=="First note content here.", r.text)
r = requests.post(f"{BASE}/notes/{note1_id}/history/{oldest_version_id}/restore", headers=h(token1)); check("POST restore version → 200", r.status_code==200, r.text); check("POST restore version → restores old content", r.json().get("content")=="First note content here.", r.text)
r = requests.get(f"{BASE}/notes/{note1_id}/history", headers=h(token1)); check("GET history after restore → 3 entries", r.status_code==200 and len(r.json())==3, r.text)
r = requests.put(f"{BASE}/notes/{note1_id}", headers=h(token2), json={"title":"Hacked","content":"bad"}); check("PUT /notes/{id} other user → 403", r.status_code==403, r.text)

# SHARE
r = requests.post(f"{BASE}/notes/{note1_id}/share", headers=h(token1), json={"share_with_email":email2}); check("POST /notes/{id}/share → 200", r.status_code==200, r.text)
r = requests.get(f"{BASE}/notes/{note1_id}", headers=h(token2)); check("GET shared note by shared user → 200", r.status_code==200, r.text)
r = requests.get(f"{BASE}/notes/{note1_id}/history", headers=h(token2)); check("GET history by shared user → 403", r.status_code==403, r.text)
r = requests.post(f"{BASE}/notes/{note1_id}/history/{oldest_version_id}/restore", headers=h(token2)); check("POST restore by shared user → 403", r.status_code==403, r.text)
r = requests.get(f"{BASE}/notes", headers=h(token2)); check("GET /notes shared user sees shared note", any(n["id"]==note1_id for n in r.json()), str(r.json())[:100])
r = requests.post(f"{BASE}/notes/{note1_id}/share", headers=h(token1), json={"share_with_email":email2}); check("POST /share duplicate → 409", r.status_code==409, r.text)
r = requests.post(f"{BASE}/notes/{note1_id}/share", headers=h(token1), json={"share_with_email":email1}); check("POST /share with self → 400", r.status_code==400, r.text)
r = requests.post(f"{BASE}/notes/{note1_id}/share", headers=h(token1), json={"share_with_email":"nobody@nowhere.com"}); check("POST /share unknown email → 404", r.status_code==404, r.text)
r = requests.put(f"{BASE}/notes/{note1_id}", headers=h(token2), json={"title":"Hijack","content":"bad"}); check("PUT by shared user → 403", r.status_code==403, r.text)
r = requests.delete(f"{BASE}/notes/{note1_id}", headers=h(token2)); check("DELETE by shared user → 403", r.status_code==403, r.text)

# PIN
r = requests.patch(f"{BASE}/notes/{note1_id}/pin", headers=h(token1)); check("PATCH /pin → 200", r.status_code==200, r.text); check("PATCH /pin → pinned=true", r.json().get("pinned")==True, r.text)
r = requests.patch(f"{BASE}/notes/{note1_id}/pin", headers=h(token1)); check("PATCH /pin toggle → pinned=false", r.json().get("pinned")==False, r.text)

# SEARCH
r = requests.get(f"{BASE}/search?q=First", headers=h(token1)); check("GET /search → 200", r.status_code==200, r.text[:80]); check("GET /search → finds result", len(r.json())>0, r.text[:80])
r = requests.get(f"{BASE}/search?q=xyznonexistent999", headers=h(token1)); check("GET /search no results → empty list", r.status_code==200 and r.json()==[], r.text)
r = requests.get(f"{BASE}/search?q=test"); check("GET /search no auth → 401", r.status_code==401, r.text)

# DELETE
r = requests.delete(f"{BASE}/notes/{note2_id}", headers=h(token1)); check("DELETE /notes/{id} → 204", r.status_code==204, r.text); check("DELETE /notes/{id} → empty body", r.text=="", f"body='{r.text}'")
r = requests.get(f"{BASE}/notes/{note2_id}", headers=h(token1)); check("GET after delete → 404", r.status_code==404, r.text)

print(f"\n{'='*40}")
print(f"PASSED: {PASS}  |  FAILED: {FAIL}  |  TOTAL: {PASS+FAIL}")
print(f"{'🎉 ALL PASSED' if FAIL==0 else f'⚠️ {FAIL} FAILED'}")
sys.exit(0 if FAIL==0 else 1)
