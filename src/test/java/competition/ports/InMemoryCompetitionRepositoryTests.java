package competition.ports;

class InMemoryCompetitionRepositoryTests extends CompetitionRepositoryContract {
    @Override
    protected CompetitionRepository competitions() { return new InMemoryCompetitionRepository(); }
}
