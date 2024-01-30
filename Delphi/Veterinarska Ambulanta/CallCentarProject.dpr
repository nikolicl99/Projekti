program CallCentarProject;

uses
  Vcl.Forms,
  CallCentar in 'CallCentar.pas' {CCForm1},
  CallCentarOdgovor in 'CallCentarOdgovor.pas' {CCForm2},
  CallCentarOdgovorVeterinara in 'CallCentarOdgovorVeterinara.pas' {CCForm3};

{$R *.res}

begin
  Application.Initialize;
  Application.MainFormOnTaskbar := True;
  Application.CreateForm(TCCForm1, CCForm1);
  Application.CreateForm(TCCForm2, CCForm2);
  Application.CreateForm(TCCForm3, CCForm3);
  Application.Run;
end.
