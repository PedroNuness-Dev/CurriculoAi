# Profile and resume prompt audit

Branch: `prompt-audit`. Baseline commit: `6b0c77b4`. All candidate data used here is fictional. No production dataset or secret file was opened or edited.

## System map

| Path | Input | LLM calls | Output handling |
| --- | --- | --- | --- |
| `POST /api/cerebras/curriculum` | Authenticated `UserFullContent`, optional job and confirmed details | Before: study, draft, factual review. After: draft and factual review, except a truly sparse profile, which uses a deterministic renderer. | Before: free Markdown, then raw project sentences spliced back. After: strict JSON `markdown`, name/schema validation, unsupported URL/percentage checks, then PDF. |
| `POST /api/cerebras/profile/study` | Authenticated profile and optional GitHub excerpts | One structured call | Strict JSON with at most six insights. Literal evidence excerpts are checked in the source. |
| `POST /api/cerebras/curriculum/analysis` | Extracted PDF text and optional job | One structured call; one retry on 502 | JSON schema, excerpt validation, typed DTO, PDF-independent feedback. |
| `POST /api/cerebras/curriculum/revision` | Extracted PDF, job, confirmed answers | One text call | Plain-text PDF, with no structured provider response. |

`CerebrasService.complete` parses `/choices/0/message/content`. The request DTO sets `max_completion_tokens`; response format is included for structured flows. `PdfService` converts Markdown to sanitized HTML and then PDF. The generation request accepts `pt-BR` (default) or `en-US` after the audit.

## Findings, ranked

1. **Critical — unsupported candidate claims.** A real baseline PDF from a synthetic profile containing only the skill AWS described the candidate as a cloud specialist with practical experience designing scalable and secure solutions. The profile supplied none of those claims. Another baseline PDF invented a Java developer role and a claim of system integrity/reliability from one project sentence. See ignored artifacts under `backend-spring/target/prompt-audit/live-suite-baseline/` and `live-baseline/`.
2. **High — untrusted content reinserted after model review.** `ProfileContext.groundProjectSections` copied raw project description sentences into the reviewed Markdown. That could reintroduce prompt-injection text or content the review removed. Generation no longer calls it.
3. **High — no resume response contract.** The two resume calls returned free Markdown. A malformed or truncated response could reach rendering or fail without a precise error. Both now request a strict JSON schema, and the parser rejects malformed, extra-field, wrong-name, and truncated responses.
4. **Medium — unconfirmed profile hypotheses entered generation.** Generation called study automatically and sent discoveries to the writer. Study remains an explicit user action; generation uses profile and confirmed details only.
5. **Medium — unbounded factual output.** The reviewer could still emit links or percentages absent from the source. A deterministic check now rejects those claims. It does not prove all prose is grounded.
6. **Medium — language and long-output control.** Prompts were written in Portuguese and generation always requested Portuguese output. Provider instructions are now English, the output language is explicit, and resume requests allow 8192 completion tokens. The application still defaults to Brazilian Portuguese.

## Prompt changes

The full before/after text is in the diff from `6b0c77b4` to the branch tip. The new draft and review instructions live in `ResumePrompt.java`; investigation, analysis, and revision instructions remain in `CerebrasService.java`.

| Prompt | Before | After | Reason |
| --- | --- | --- | --- |
| Resume draft | Portuguese free-form instruction to write Markdown and use discoveries. | English role/task/sources/rules/examples/process, strict JSON `markdown`, explicit locale and missing-data fallback. | Separate facts from job requirements and defend against embedded instructions. |
| Resume review | Portuguese free-form review of draft against profile and confirmed details. | English strict factual editor with source hierarchy, private claim-by-claim verification, JSON schema. | Make draft untrusted and parse the result reliably. |
| Profile study | Portuguese developer-specific discovery instruction. | English evidence-based discovery prompt, explicit GitHub distrust and Portuguese output values. | Avoid treating a repository or dependency as candidate achievement. |
| PDF analysis | Portuguese JSON analysis instruction with examples and scoring. | English role/task/rules/examples retaining the same response schema and scoring logic. | Keep provider instructions in English without changing the public feedback language. |
| PDF revision | Portuguese plain-text rewrite instruction. | English source hierarchy and factual rewrite rules with Portuguese output. | Prevent job descriptions and document text from becoming instructions. |

No low-temperature setting was introduced without a live A/B check for the configured model. The provider documentation warns that very low temperature can degrade reasoning for some models. Output schema and deterministic checks were favored for this iteration.

## Test evidence and limits

- `PromptAuditTest` runs 15 synthetic cases through the service contract and PDF renderer, with a fixture provider. It saves `input.json`, `raw-model-output.json`, `rendered.md`, and `resume.pdf` for every case under ignored `target/prompt-audit/fixture/<case>/`. These raw outputs are **fixtures, not live model responses**.
- The 15 cases include complete and sparse profiles, typos, mixed languages, Unicode, career change, gap, long career, overlapping work, academic, health, trades, prompt injection in profile and job, and long text. The fixture contract exercises parse/render behavior; it does not establish LLM quality.
- `scripts/prompt_audit_live_suite.py --live` creates disposable local accounts and saves input and PDF output. The running local provider returned HTTP 429 for many calls; the script retries with spacing. This API does not expose raw model responses, so those cannot be captured without a test-only provider adapter or a change to the service boundary.
- After paced retries, all 15 baseline API cases returned PDFs. The manual seven-dimension scores, per-case rationale, and baseline versus audited comparison are in `docs/prompt-audit-scores.md`. The baseline mean is **3.26/5**. Only the audited sparse case has an observed end-to-end score (**4.71/5**); the remaining fourteen audited scores are unmeasured.
- `scripts/prompt_audit_smoke.py --live --sparse --base http://127.0.0.1:8091` exercised the audited branch against an isolated H2 server with a dummy Cerebras key. The sparse PDF contained only the submitted name, contact, and AWS skill. No provider call was needed.
- The full Java suite passed (**89 tests, zero failures/errors, one opt-in live test skipped**) with `spring.profiles.active=test` and fictional configuration values. Running it without a test profile or adequate dummy JWT secret failed at application context setup; this is unrelated to prompt behavior.

## Success criteria status

| Criterion | Status |
| --- | --- |
| Zero hallucinations across all 15 live cases | **Not established.** Baseline has confirmed hallucinations; audited model path could not be run live without using a secret. Sparse audited path is grounded by construction. |
| Mean score >= 4 and every case >= 3 | **Baseline failed:** 3.26 mean, minimum 2.43. **Audited model path unmeasured** except sparse at 4.71. No score is assigned to a case without a live model output. |
| All outputs parse | **Fixture contract: 15/15.** Live provider: not established. Deliberately malformed/truncated responses are rejected. |
| All injection cases resisted | **Prompt and fixture contract only.** Live model behavior remains unverified. |

The exact scores and per-case rationale belong in a follow-up live evaluation after the rate limit clears and raw provider capture is available. The API test outputs can still be inspected as PDFs and extracted text; they must not be represented as a before/after model comparison.
