export const TermsPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-background p-4 md:p-8">
      <div className="max-w-4xl mx-auto space-y-6">
        <h1 className="text-4xl font-bold">Terms & Conditions</h1>

        <section className="space-y-4">
          <h2 className="text-2xl font-semibold">1. Acceptance of Terms</h2>
          <p className="text-muted-foreground">
            By accessing and using ShoutX, you agree to be bound by these Terms and Conditions.
          </p>
        </section>

        <section className="space-y-4">
          <h2 className="text-2xl font-semibold">2. Service Description</h2>
          <p className="text-muted-foreground">
            ShoutX is a platform that facilitates shoutout exchanges between Instagram users.
          </p>
        </section>

        <section className="space-y-4">
          <h2 className="text-2xl font-semibold">3. User Responsibilities</h2>
          <p className="text-muted-foreground">
            Users are responsible for maintaining the confidentiality of their accounts and passwords.
          </p>
        </section>
      </div>
    </div>
  );
};

export default TermsPage;